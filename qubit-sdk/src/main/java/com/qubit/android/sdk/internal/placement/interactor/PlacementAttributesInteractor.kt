package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonObject
import com.qubit.android.sdk.api.tracker.event.QBEvent

interface PlacementAttributesInteractor {

  fun storeEventAttribute(event: QBEvent)

  fun loadAttributesMap(): Map<String, JsonObject>
}
