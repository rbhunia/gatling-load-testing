package simulations

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature

object JsonMapper {
  // Configure ObjectMapper for optimal performance
  val mapper: ObjectMapper = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    
  // Pre-create reusable node for better performance
  def createPayloadJson(xmlContent: String): String = {
    val rootNode = mapper.createObjectNode()
    rootNode.put("payload", xmlContent)
    mapper.writeValueAsString(rootNode)
  }
}
