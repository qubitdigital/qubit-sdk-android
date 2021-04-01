package com.qubit.android.sdk.api.placement

import com.google.gson.JsonElement

interface Placement : PlacementCallbackConnector {
  val content: JsonElement
  val impressionUrl: String
  val clickthroughUrl: String
}
