package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonArray
import com.google.gson.JsonElement
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
  ): JsonObject {
    val attributesJson = JsonObject()

    // 1 visitor attributes should be set by SDK
    attributesJson.add(VISITOR_ATTRIBUTE_KEY, buildVisitorAttributesJson(deviceId))

    // 2 custom attributes passed by SDK user
    if (customAttributes != null) {
      customAttributes.remove(VISITOR_ATTRIBUTE_KEY)
      for (key in customAttributes.keySet()) {
        // user/view attributes should contain all fields from schema and nothing more
        val customAttribute = prepareCustomAttribute(key, customAttributes.get(key))
        attributesJson.add(key, customAttribute)
      }
    }

    // 3 cached attributes - skipped if set by SDK user
    for (key in cachedAttributes.keys) {
      if (!attributesJson.has(key)) {
        attributesJson.add(key, cachedAttributes[key])
      }
    }

    // 4 if user/view attributes are missing, then we should add empty ones
    if (!attributesJson.has(USER_ATTRIBUTE_KEY)) {
      attributesJson.add(USER_ATTRIBUTE_KEY, buildEmptyUserAttribute())
    }
    if (!attributesJson.has(VIEW_ATTRIBUTE_KEY)) {
      attributesJson.add(VIEW_ATTRIBUTE_KEY, buildEmptyViewAttribute())
    }
    return attributesJson
  }

  private fun prepareCustomAttribute(key: String, customValue: JsonElement): JsonElement {
    return when (key) {
      USER_ATTRIBUTE_KEY -> convertToUserAttribute(customValue)
      VIEW_ATTRIBUTE_KEY -> convertToViewAttribute(customValue)
      else -> customValue
    }
  }

  internal fun buildVisitorAttributesJson(deviceId: String) = JsonObject().apply {
    addProperty("id", deviceId)
//    addProperty("url", "")  // TODO set value
//    addProperty("userAgentString", "")  // TODO set value
  }

  private fun convertToUserAttribute(customValue: JsonElement): JsonObject =
      if (customValue is JsonObject) {
        JsonObject().apply {
          add("name", getStringValueOrDefault(customValue, "name"))
          add("email", getStringValueOrDefault(customValue, "email"))
        }
      } else {
        buildEmptyUserAttribute()
      }

  private fun buildEmptyUserAttribute() = JsonObject().apply {
    addProperty("name", "")
    addProperty("email", "")
  }

  private fun convertToViewAttribute(customValue: JsonElement): JsonObject {
    return if (customValue is JsonObject) {
      JsonObject().apply {
        add("currency", getStringValueOrDefault(customValue, "currency"))
        add("type", getStringValueOrDefault(customValue, "type"))
        add("subtypes", getArrayValueOrDefault(customValue, "subtypes"))
        add("language", getStringValueOrDefault(customValue, "language"))
      }
    } else {
      buildEmptyViewAttribute()
    }
  }

  private fun buildEmptyViewAttribute() = JsonObject().apply {
    addProperty("currency", "")
    addProperty("type", "")
    add("subtypes", JsonArray())
    addProperty("language", "")
  }

  private fun getStringValueOrDefault(jsonElement: JsonObject, propertyName: String): JsonElement {
    return (jsonElement.get(propertyName) as? JsonPrimitive)?.let { jsonPrimitive ->
      if (jsonPrimitive.isString) {
        jsonPrimitive
      } else {
        JsonPrimitive("")
      }
    } ?: JsonPrimitive("")
  }

  private fun getArrayValueOrDefault(jsonElement: JsonObject, propertyName: String): JsonElement {
    return (jsonElement.get(propertyName) as? JsonArray) ?: JsonArray()
  }
}
