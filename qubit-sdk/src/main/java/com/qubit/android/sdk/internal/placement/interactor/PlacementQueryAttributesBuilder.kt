package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.qubit.android.sdk.internal.placement.repository.PlacementAttributesRepository

internal class PlacementQueryAttributesBuilder(
    private val placementAttributesRepository: PlacementAttributesRepository
) {

  companion object {
    internal const val VISITOR_ATTRIBUTE_KEY = "visitor"
    internal const val USER_ATTRIBUTE_KEY = "user"
    internal const val VIEW_ATTRIBUTE_KEY = "view"
  }

  internal fun buildJson(
      deviceId: String,
      customAttributes: JsonObject?
  ): JsonObject {
    val attributesJson = JsonObject()

    // 1 visitor attributes should be set by SDK
    attributesJson.add(VISITOR_ATTRIBUTE_KEY, buildVisitorAttributesJson(deviceId))

    // 2 cached attributes - skipped if set by SDK user
    val cachedAttributes = placementAttributesRepository.load()
    for (key in cachedAttributes.keys) {
      if (key != VISITOR_ATTRIBUTE_KEY && (customAttributes == null || !customAttributes.has(key))) {
        attributesJson.add(key, cachedAttributes[key])
      }
    }

    // 3 custom attributes passed by SDK user
    if (customAttributes != null) {
      customAttributes.remove(VISITOR_ATTRIBUTE_KEY)
      for (key in customAttributes.keySet()) {
        // user/view attributes should contain all fields from schema and nothing more
        val customAttribute = prepareCustomAttribute(key, customAttributes.get(key))
        attributesJson.add(key, customAttribute)
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
    return if (customValue is JsonObject) {
      when (key) {
        USER_ATTRIBUTE_KEY -> convertTodUserAttribute(customValue)
        VIEW_ATTRIBUTE_KEY -> convertToViewAttribute(customValue)
        else -> customValue
      }
    } else {
      customValue
    }
  }

  internal fun buildVisitorAttributesJson(deviceId: String) = JsonObject().apply {
    addProperty("id", deviceId)
//    addProperty("url", "")  // TODO set value
//    addProperty("userAgentString", "")  // TODO set value
  }

  private fun convertTodUserAttribute(customValue: JsonObject) = JsonObject().apply {
    add("name", customValue.get("name") ?: JsonPrimitive(""))
    add("email", customValue.get("email") ?: JsonPrimitive(""))
  }

  private fun buildEmptyUserAttribute() = JsonObject().apply {
    addProperty("name", "")
    addProperty("email", "")
  }

  private fun convertToViewAttribute(customValue: JsonObject) = JsonObject().apply {
    add("currency", customValue.get("currency") ?: JsonPrimitive(""))
    add("type", customValue.get("type") ?: JsonPrimitive(""))
    add("subtypes", customValue.get("subtypes") ?: JsonArray())
    add("language", customValue.get("language") ?: JsonPrimitive(""))
  }

  private fun buildEmptyViewAttribute() = JsonObject().apply {
    addProperty("currency", "")
    addProperty("type", "")
    add("subtypes", JsonArray())
    addProperty("language", "")
  }
}
