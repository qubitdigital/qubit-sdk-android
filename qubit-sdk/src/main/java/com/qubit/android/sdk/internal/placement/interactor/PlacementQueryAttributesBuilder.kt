package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

internal class PlacementQueryAttributesBuilder {

  companion object {
    internal const val VISITOR_ATTRIBUTE_KEY = "visitor"
    internal const val USER_ATTRIBUTE_KEY = "user"
    internal const val VIEW_ATTRIBUTE_KEY = "view"
  }

  internal fun buildJson(
      deviceId: String,
      customAttributes: JsonObject?,
      cachedAttributes: Map<String, JsonObject>
  ) = JsonObject().apply {
    add(VISITOR_ATTRIBUTE_KEY, buildVisitorAttributesJson(deviceId))
    add(USER_ATTRIBUTE_KEY, customAttributes, cachedAttributes,
        { convertToUserAttribute(it) }, { buildEmptyUserAttribute() })
    add(VIEW_ATTRIBUTE_KEY, customAttributes, cachedAttributes,
        { convertToViewAttribute(it) }, { buildEmptyViewAttribute() })

    customAttributes?.keySet()
        ?.filter { !has(it) }
        ?.forEach { add(it, customAttributes.get(it)) }
  }

  private fun JsonObject.add(
      key: String,
      customAttributes: JsonObject?,
      cachedAttributes: Map<String, JsonObject>,
      converter: (JsonObject) -> JsonObject,
      emptyBuilder: () -> JsonObject
  ) {
    val value = (customAttributes?.get(key) as? JsonObject)
        // custom attributes passed by SDK user - should contain all the fields from schema and nothing more
        ?.let { converter(it) }
    // cached attributes - skipped if set by SDK user
        ?: cachedAttributes[key]
        // if expected attribute is missing, then we should add empty ones
        ?: emptyBuilder()
    add(key, value)
  }

  internal fun buildVisitorAttributesJson(deviceId: String) = JsonObject().apply {
    addProperty("id", deviceId)
//    addProperty("url", "")  // TODO set value
//    addProperty("userAgentString", "")  // TODO set value
  }

  private fun convertToUserAttribute(sourceJsonObject: JsonObject) = JsonObject().apply {
    addStringValueOrDefault(sourceJsonObject, "name")
    addStringValueOrDefault(sourceJsonObject, "email")
  }

  private fun buildEmptyUserAttribute() = JsonObject().apply {
    addProperty("name", "")
    addProperty("email", "")
  }

  private fun convertToViewAttribute(sourceJsonObject: JsonObject) = JsonObject().apply {
    addStringValueOrDefault(sourceJsonObject, "currency")
    addStringValueOrDefault(sourceJsonObject, "type")
    addArrayValueOrDefault(sourceJsonObject, "subtypes")
    addStringValueOrDefault(sourceJsonObject, "language")
  }

  private fun buildEmptyViewAttribute() = JsonObject().apply {
    addProperty("currency", "")
    addProperty("type", "")
    add("subtypes", JsonArray())
    addProperty("language", "")
  }

  private fun JsonObject.addStringValueOrDefault(sourceJsonObject: JsonObject, propertyName: String) {
    val value = (sourceJsonObject.get(propertyName) as? JsonPrimitive)
        ?.takeIf { it.isString }
        ?: JsonPrimitive("")
    add(propertyName, value)
  }

  private fun JsonObject.addArrayValueOrDefault(sourceJsonObject: JsonObject, propertyName: String) {
    val value = (sourceJsonObject.get(propertyName) as? JsonArray) ?: JsonArray()
    add(propertyName, value)
  }
}
