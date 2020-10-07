package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.qubit.android.sdk.internal.placement.repository.PlacementAttributesRepository

class PlacementQueryAttributesBuilder(
    private val placementAttributesRepository: PlacementAttributesRepository
) {

  companion object {
    internal const val VISITOR_ATTRIBUTE_KEY = "visitor"
    internal const val USER_ATTRIBUTE_KEY = "user"
    internal const val VIEW_ATTRIBUTE_KEY = "view"
  }

  internal fun buildJson(
      deviceId: String,
      userAttributes: JsonObject?
  ): JsonObject {
    val attributesJson = JsonObject()

    // 1 visitor attributes should be set by SDK
    attributesJson.add(VISITOR_ATTRIBUTE_KEY, buildVisitorAttributesJson(deviceId))

    // 2 cached attributes - skipped if set by SDK user
    val cachedAttributes = placementAttributesRepository.load()
    for (key in cachedAttributes.keys) {
      if (key != VISITOR_ATTRIBUTE_KEY && (userAttributes == null || !userAttributes.has(key))) {
        attributesJson.add(key, cachedAttributes[key])
      }
    }

    // 3 attributes passed by SDK user
    if (userAttributes != null) {
      userAttributes.remove(VISITOR_ATTRIBUTE_KEY)
      for (key in userAttributes.keySet()) {
        attributesJson.add(key, userAttributes.get(key))
      }
    }

    // 4 if user/view events are missing, then we should add empty ones
    if (!attributesJson.has(USER_ATTRIBUTE_KEY)) {
      attributesJson.add(USER_ATTRIBUTE_KEY, buildEmptyUserAttributesJson())
    }
    if (!attributesJson.has(VIEW_ATTRIBUTE_KEY)) {
      attributesJson.add(VIEW_ATTRIBUTE_KEY, buildEmptyViewAttributesJson())
    }
    return attributesJson
  }

  internal fun buildVisitorAttributesJson(deviceId: String) = JsonObject().apply {
    addProperty("id", deviceId)
//    addProperty("url", "")  // TODO set value
//    addProperty("userAgentString", "")  // TODO set value
  }

  private fun buildEmptyUserAttributesJson() = JsonObject().apply {
    addProperty("name", "")
    addProperty("email", "")
  }

  private fun buildEmptyViewAttributesJson() = JsonObject().apply {
    addProperty("currency", "")
    addProperty("type", "")
    add("subtypes", JsonArray())
    addProperty("language", "")
  }
}
