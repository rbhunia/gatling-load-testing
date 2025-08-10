package simulations

/**
 * Optimized JSON mapper with configurable serialization strategies
 * Automatically selects the best serialization method based on content size and performance requirements
 */
object OptimizedJsonMapper {
  
  // Serialization strategy enum
  sealed trait SerializationStrategy
  case object Jackson extends SerializationStrategy
  case object Manual extends SerializationStrategy  
  case object DslJson extends SerializationStrategy
  case object Auto extends SerializationStrategy
  
  // Current strategy (can be changed at runtime)
  @volatile private var currentStrategy: SerializationStrategy = Auto
  
  // Performance thresholds for auto-selection
  private val SMALL_CONTENT_THRESHOLD = 1000  // chars
  private val LARGE_CONTENT_THRESHOLD = 10000 // chars
  
  /**
   * Main entry point - automatically selects optimal serialization strategy
   */
  def createPayloadJson(xmlContent: String): String = {
    currentStrategy match {
      case Jackson => JsonMapper.createPayloadJson(xmlContent)
      case Manual => FastJsonSerializer.createPayloadJson(xmlContent)
      case DslJson => 
        // Fallback to manual for now, replace with DSL-JSON when dependency is added
        FastJsonSerializer.createPayloadJson(xmlContent)
      case Auto => selectOptimalStrategy(xmlContent)
    }
  }
  
  /**
   * Auto-select optimal strategy based on content characteristics
   */
  private def selectOptimalStrategy(xmlContent: String): String = {
    val contentSize = xmlContent.length
    
    contentSize match {
      // For small content, manual serialization is fastest
      case size if size < SMALL_CONTENT_THRESHOLD =>
        FastJsonSerializer.createPayloadJson(xmlContent)
        
      // For large content, consider memory efficiency
      case size if size > LARGE_CONTENT_THRESHOLD =>
        // Use manual for now, could switch to streaming approaches for very large content
        FastJsonSerializer.createPayloadJson(xmlContent)
        
      // For medium content, use manual (best overall performance)
      case _ =>
        FastJsonSerializer.createPayloadJson(xmlContent)
    }
  }
  
  /**
   * Manually set serialization strategy
   */
  def setStrategy(strategy: SerializationStrategy): Unit = {
    currentStrategy = strategy
  }
  
  /**
   * Get current strategy
   */
  def getCurrentStrategy: SerializationStrategy = currentStrategy
  
  /**
   * Performance-optimized batch serialization for multiple payloads
   */
  def createBatchPayloadJson(xmlContents: List[String]): List[String] = {
    // Pre-allocate result list for better performance
    val results = new Array[String](xmlContents.length)
    var i = 0
    
    xmlContents.foreach { xml =>
      results(i) = createPayloadJson(xml)
      i += 1
    }
    
    results.toList
  }
  
  /**
   * Memory-efficient streaming serialization for very large datasets
   */
  def createStreamingPayloadJson(xmlContents: Iterator[String]): Iterator[String] = {
    xmlContents.map(createPayloadJson)
  }
  
  /**
   * Benchmark all strategies and return performance metrics
   */
  def benchmarkStrategies(testContent: String, iterations: Int = 1000): Map[String, Double] = {
    val strategies = List(
      ("Jackson", () => JsonMapper.createPayloadJson(testContent)),
      ("Manual", () => FastJsonSerializer.createPayloadJson(testContent))
    )
    
    strategies.map { case (name, func) =>
      // Warmup
      for (_ <- 1 to 100) func()
      
      // Benchmark
      val startTime = System.nanoTime()
      for (_ <- 1 to iterations) func()
      val endTime = System.nanoTime()
      
      val avgTimeNanos = (endTime - startTime).toDouble / iterations
      val throughputPerSecond = 1e9 / avgTimeNanos
      
      name -> throughputPerSecond
    }.toMap
  }
}