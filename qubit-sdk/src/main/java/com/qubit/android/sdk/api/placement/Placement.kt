package com.qubit.android.sdk.api.placement

import com.google.gson.JsonObject

interface Placement : PlacementCallbackConnector {
  val content: JsonObject
}
