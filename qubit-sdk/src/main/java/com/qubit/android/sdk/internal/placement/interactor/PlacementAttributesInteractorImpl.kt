package com.qubit.android.sdk.internal.placement.interactor

import com.qubit.android.sdk.api.tracker.event.QBEvent
import com.qubit.android.sdk.internal.placement.interactor.PlacementQueryAttributesBuilder.Companion.USER_ATTRIBUTE_KEY
import com.qubit.android.sdk.internal.placement.interactor.PlacementQueryAttributesBuilder.Companion.VIEW_ATTRIBUTE_KEY
import com.qubit.android.sdk.internal.placement.repository.PlacementAttributesRepository

class PlacementAttributesInteractorImpl(
    private val placementAttributesRepository: PlacementAttributesRepository
) : PlacementAttributesInteractor {

  override fun storeEventAttribute(event: QBEvent) {
    mapTypeToKey(event.type)?.let {
      placementAttributesRepository.save(it, event.toJsonObject())
    }
  }

  private fun mapTypeToKey(eventType: String): String? = when (eventType) {
    "ecView", "trView" -> VIEW_ATTRIBUTE_KEY
    "ecUser", "trUser" -> USER_ATTRIBUTE_KEY
    else -> null
  }

  override fun loadAttributesMap() = placementAttributesRepository.load()
}
