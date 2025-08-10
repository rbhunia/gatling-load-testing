package simulations

import scala.util.Random
import java.util.UUID

object DataPool {
  // Pre-generate static data for better performance
  private val currencies = Array("GBP", "EUR", "USD")
  private val bics = Array("BUKBGB22", "NWBKGB2L", "HBUKGB4B", "LOYDGB2L")
  private val messageTypes = Array("camt.054", "camt.053", "camt.052", "pacs.008")
  
  // Pre-generated account IDs for reuse
  private val accountIds = (1 to 1000).map(i => f"ACC$i%06d").toArray
  
  // Thread-local random instances for better performance in concurrent scenarios
  private val threadLocalRandom = new ThreadLocal[Random] {
    override def initialValue(): Random = new Random()
  }
  
  def getRandomCurrency: String = currencies(threadLocalRandom.get().nextInt(currencies.length))
  def getRandomBic: String = bics(threadLocalRandom.get().nextInt(bics.length))
  def getRandomMessageType: String = messageTypes(threadLocalRandom.get().nextInt(messageTypes.length))
  def getRandomAccountId: String = accountIds(threadLocalRandom.get().nextInt(accountIds.length))
  
  def generateRandomAmount: String = f"${threadLocalRandom.get().nextDouble() * 10000}%.2f"
  
  def generateUniqueId: String = UUID.randomUUID().toString
  
  def generateMessageId: String = s"MSG-${System.currentTimeMillis()}-${threadLocalRandom.get().nextInt(10000)}"
  
  def generateEndToEndId(uniqueId: String): String = s"E2E-${uniqueId.substring(0, 8)}"
  
  def generateInstructionId(uniqueId: String): String = s"INST-${uniqueId.substring(0, 8)}"
}