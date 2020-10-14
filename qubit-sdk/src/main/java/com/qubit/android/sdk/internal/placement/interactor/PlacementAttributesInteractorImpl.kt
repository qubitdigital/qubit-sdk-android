package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.qubit.android.sdk.internal.eventtracker.repository.EventModel
import com.qubit.android.sdk.internal.placement.interactor.PlacementQueryAttributesBuilder.Companion.USER_ATTRIBUTE_KEY
import com.qubit.android.sdk.internal.placement.interactor.PlacementQueryAttributesBuilder.Companion.VIEW_ATTRIBUTE_KEY
import com.qubit.android.sdk.internal.placement.repository.PlacementAttributesRepository

class PlacementAttributesInteractorImpl(
    private val placementAttributesRepository: PlacementAttributesRepository
) : PlacementAttributesInteractor {

  private val jsonParser = JsonParser()

  override fun storeEventAttribute(event: EventModel) {
    mapTypeToKey(event.type)?.let {
      placementAttributesRepository.save(it, event.eventBody)
    }
  }

  private fun mapTypeToKey(eventType: String): String? = when (eventType) {
    "ecView", "trView" -> VIEW_ATTRIBUTE_KEY
    "ecUser", "trUser" -> USER_ATTRIBUTE_KEY
    else -> null
  }

  override fun loadAttributesMap(): Map<String, JsonObject> {
    val storedItems = placementAttributesRepository.load()
    val result = HashMap<String, JsonObject>()
    for (key in storedItems.keys) {
      getJsonObject(storedItems[key])?.let {
        result.put(key, it)
      }
    }
    return result
  }

  private fun getJsonObject(value: Any?): JsonObject? = try {
    jsonParser.parse(value.toString()).asJsonObject
  } catch (e: Exception) {
    null
  }
}
