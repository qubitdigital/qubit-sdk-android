package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonObject
import com.qubit.android.sdk.internal.eventtracker.repository.EventModel

interface PlacementAttributesInteractor {

  fun storeEventAttribute(event: EventModel)

  fun loadAttributesMap(): Map<String, JsonObject>
}
