package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import java.time.Instant
import java.util.UUID
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

class MXMessageLoadTest extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val authToken = "your_sso_token_here"

  // Optimized feeder using pre-generated data pools
  val uniqueMessageFeeder: Iterator[Map[String, String]] = Iterator.continually {
    val messageType = DataPool.getRandomMessageType
    val uniqueId = DataPool.generateUniqueId
    val timestamp = Instant.now().toString
    val amount = DataPool.generateRandomAmount
    val currency = DataPool.getRandomCurrency
    val accountId = DataPool.getRandomAccountId
    val messageId = DataPool.generateMessageId
    val bic = DataPool.getRandomBic
    val endToEndId = DataPool.generateEndToEndId(uniqueId)
    val instructionId = DataPool.generateInstructionId(uniqueId)

    Map(
      "messageType" -> messageType,
      "uniqueId" -> uniqueId,
      "messageId" -> messageId,
      "timestamp" -> timestamp,
      "amount" -> amount,
      "currency" -> currency,
      "accountId" -> accountId,
      "bic" -> bic,
      "endToEndId" -> endToEndId,
      "instructionId" -> instructionId,
      "payload" -> generateMXMessagePayload(messageType, uniqueId, messageId, timestamp, amount, currency, accountId, bic, endToEndId, instructionId)
    )
  }

  // Optimized function to generate unique MX message XML wrapped in JSON
  def generateMXMessagePayload(messageType: String, uniqueId: String, messageId: String,
                               timestamp: String, amount: String, currency: String, accountId: String,
                               bic: String, endToEndId: String, instructionId: String): String = {

    val mxXml = messageType match {
      case "camt.054" => Camt054Generator.generate(uniqueId, messageId, timestamp, amount, currency, accountId, bic)
      case "camt.053" => Camt053Generator.generate(uniqueId, messageId, timestamp, amount, currency, accountId, bic)
      case "camt.052" => Camt052Generator.generate(uniqueId, messageId, timestamp, amount, currency, accountId, bic)
      case "pacs.008" => Pacs008Generator.generate(uniqueId, messageId, timestamp, amount, currency, accountId, bic, endToEndId, instructionId)
    }

    // Use optimized serialization for maximum performance
    OptimizedJsonMapper.createPayloadJson(mxXml)
  }

  val normalLoadScenario: ScenarioBuilder = scenario("Normal Load Test - Unique MX Messages")
    .feed(uniqueMessageFeeder)
    .exec(
      http("Process Unique MX Message")
        .post("""/api/mx-messages""") // Replace it with your actual endpoint
        .header("Cookie", authToken)
        .body(StringBody("${payload}"))
        .check(
          status.is(200),
          responseTimeInMillis.lt(5000),
          // Optional: Check response contains success indicator
          jsonPath("$.status").optional.saveAs("responseStatus")
        )
    )
    .pause(1) // Add a small pause between requests

  val stressTestScenario: ScenarioBuilder = scenario("Stress Test - 150% Load")
    .feed(uniqueMessageFeeder)
    .exec(
      http("Process MX Message - Stress")
        .post("/api/mx-messages")
        .header("Cookie", authToken)
        .body(StringBody("${payload}"))
        .check(
          status.in(200, 201, 202),
          responseTimeInMillis.lt(10000)
        )
    )

  val spikeTestScenario: ScenarioBuilder = scenario("Spike Test - 500% Load")
    .feed(uniqueMessageFeeder)
    .exec(
      http("Process MX Message - Spike")
        .post("/api/mx-messages")
        .header("Cookie", authToken)
        .body(StringBody("${payload}"))
        .check(
          status.in(200, 201, 202, 429), // Accept rate limiting
          responseTimeInMillis.lt(15000)
        )
    )

  setUp(
    // Normal Load Test - 2000 messages per hour = ~0.56 messages per second
    normalLoadScenario.inject(
      constantUsersPerSec(0.1) during (2 minutes), // Warm up
      constantUsersPerSec(0.56) during (1 hour) // Normal load
    ).protocols(httpProtocol),

    // Stress Test - 150% of a normal load
    stressTestScenario.inject(
      constantUsersPerSec(0.84) during (30 minutes)
    ).protocols(httpProtocol),

    // Spike Test - 500% of a normal load for a short duration
    spikeTestScenario.inject(
      constantUsersPerSec(0.56) during (5 minutes), // Normal load
      constantUsersPerSec(2.8) during (2 minutes), // Spike
      constantUsersPerSec(0.56) during (5 minutes) // Recovery
    ).protocols(httpProtocol)
  ).assertions(
    global.responseTime.percentile3.lt(3000), // 95th percentile < 3s
    global.responseTime.percentile4.lt(5000), // 99th percentile < 5s
    global.successfulRequests.percent.gt(99.5), // 99.5% success rate
    global.responseTime.mean.lt(2000) // Mean response time < 2s
  )


}
