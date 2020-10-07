package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonObject
import com.qubit.android.sdk.internal.placement.repository.PlacementAttributesRepository

class PlacementQueryAttributesBuilder(
    private val placementAttributesRepository: PlacementAttributesRepository
) {

  companion object {
    internal const val VISITOR_ATTRIBUTE_KEY = "visitor"
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
    return attributesJson
  }

  internal fun buildVisitorAttributesJson(deviceId: String) = JsonObject().apply {
    addProperty("id", deviceId)
//    addProperty("url", "")  // TODO set value
//    addProperty("userAgentString", "")  // TODO set value
  }
}
