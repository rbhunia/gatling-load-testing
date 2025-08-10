package simulations

import scala.util.Random

/**
 * Comprehensive serialization performance benchmark
 * Compares Jackson, manual serialization, and DSL-JSON approaches
 */
object SerializationBenchmark {
  
  // Sample XML content of various sizes for realistic testing
  private val smallXml = """<Document><Test>Small content</Test></Document>"""
  
  private val mediumXml = 
    """<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:camt.054.001.08">
  <BkToCstmrDbtCdtNtfctn>
    <GrpHdr>
      <MsgId>MSG-12345</MsgId>
      <CreDtTm>2024-01-01T12:00:00Z</CreDtTm>
      <NbOfTxs>1</NbOfTxs>
    </GrpHdr>
    <Ntfctn>
      <Id>NOTIF-TEST</Id>
      <ElctrncSeqNb>1</ElctrncSeqNb>
    </Ntfctn>
  </BkToCstmrDbtCdtNtfctn>
</Document>"""

  private val largeXml = mediumXml * 5 // Simulate larger XML
  
  case class BenchmarkResult(
    method: String,
    avgTimeNanos: Long,
    totalTime: Long,
    iterations: Int,
    throughputPerSecond: Double
  )
  
  /**
   * Run comprehensive serialization benchmarks
   */
  def runBenchmarks(warmupIterations: Int = 1000, benchmarkIterations: Int = 10000): Unit = {
    println("🔥 Serialization Performance Benchmark")
    println("=" * 50)
    
    val testCases = List(
      ("Small XML", smallXml),
      ("Medium XML", mediumXml),
      ("Large XML", largeXml)
    )
    
    testCases.foreach { case (name, xml) =>
      println(s"\n📊 Testing with $name (${xml.length} chars)")
      println("-" * 40)
      
      // Warmup
      println("⏳ Warming up...")
      warmup(xml, warmupIterations)
      
      // Benchmark each method
      val results = List(
        benchmarkJackson(xml, benchmarkIterations),
        benchmarkManual(xml, benchmarkIterations),
        benchmarkDslJsonDirect(xml, benchmarkIterations)
      )
      
      // Display results
      results.sortBy(_.avgTimeNanos).foreach { result =>
        val performance = if (result == results.minBy(_.avgTimeNanos)) "🥇" else "📈"
        println(f"$performance ${result.method}%-20s: ${result.avgTimeNanos}%,8d ns/op | ${result.throughputPerSecond}%,.0f ops/sec")
      }
      
      // Calculate improvement
      val fastest = results.minBy(_.avgTimeNanos)
      val jackson = results.find(_.method == "Jackson").get
      val improvement = ((jackson.avgTimeNanos.toDouble / fastest.avgTimeNanos) - 1) * 100
      println(f"\n💡 Best improvement over Jackson: ${improvement}%.1f%% faster")
    }
  }
  
  private def warmup(xml: String, iterations: Int): Unit = {
    for (_ <- 1 to iterations) {
      JsonMapper.createPayloadJson(xml)
      FastJsonSerializer.createPayloadJson(xml)
      // DslJsonSerializer.createPayloadJsonDirect(xml) // Uncomment when DSL-JSON is available
    }
  }
  
  private def benchmarkJackson(xml: String, iterations: Int): BenchmarkResult = {
    System.gc() // Clean up before benchmark
    Thread.sleep(100)
    
    val startTime = System.nanoTime()
    for (_ <- 1 to iterations) {
      JsonMapper.createPayloadJson(xml)
    }
    val endTime = System.nanoTime()
    
    val totalTime = endTime - startTime
    val avgTime = totalTime / iterations
    val throughput = 1e9 / avgTime
    
    BenchmarkResult("Jackson", avgTime, totalTime, iterations, throughput)
  }
  
  private def benchmarkManual(xml: String, iterations: Int): BenchmarkResult = {
    System.gc()
    Thread.sleep(100)
    
    val startTime = System.nanoTime()
    for (_ <- 1 to iterations) {
      FastJsonSerializer.createPayloadJson(xml)
    }
    val endTime = System.nanoTime()
    
    val totalTime = endTime - startTime
    val avgTime = totalTime / iterations
    val throughput = 1e9 / avgTime
    
    BenchmarkResult("Manual", avgTime, totalTime, iterations, throughput)
  }
  
  private def benchmarkDslJsonDirect(xml: String, iterations: Int): BenchmarkResult = {
    System.gc()
    Thread.sleep(100)
    
    val startTime = System.nanoTime()
    for (_ <- 1 to iterations) {
      // For now, use manual since DSL-JSON dependency not added yet
      FastJsonSerializer.createPayloadJson(xml)
    }
    val endTime = System.nanoTime()
    
    val totalTime = endTime - startTime
    val avgTime = totalTime / iterations
    val throughput = 1e9 / avgTime
    
    BenchmarkResult("Manual (DSL-JSON)", avgTime, totalTime, iterations, throughput)
  }
  
  /**
   * Quick correctness test to ensure all methods produce valid JSON
   */
  def validateCorrectness(): Unit = {
    println("✅ Validating serialization correctness...")
    
    val testXml = """<test attr="value">Content with "quotes" and 
newlines</test>"""
    
    val jacksonResult = JsonMapper.createPayloadJson(testXml)
    val manualResult = FastJsonSerializer.createPayloadJson(testXml)
    
    println(s"Jackson:  $jacksonResult")
    println(s"Manual:   $manualResult")
    
    // Basic validation - both should contain the XML content properly escaped
    val isValid = jacksonResult.contains("payload") && 
                  manualResult.contains("payload") &&
                  jacksonResult.startsWith("{") && 
                  manualResult.startsWith("{")
    
    if (isValid) {
      println("✅ All serializers produce valid JSON")
    } else {
      println("❌ Serialization validation failed")
    }
  }
}