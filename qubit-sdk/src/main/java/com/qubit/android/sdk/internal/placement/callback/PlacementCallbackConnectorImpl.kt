package com.qubit.android.sdk.internal.placement.callback

import com.qubit.android.sdk.api.placement.PlacementCallbackConnector
import com.qubit.android.sdk.internal.callbacktracker.CallbackRequestTracker

internal class PlacementCallbackConnectorImpl(
    private val callbackRequestTracker: CallbackRequestTracker,
    private val impressionUrl: String,
    private val clickthroughUrl: String
) : PlacementCallbackConnector {

  override fun impression() {
    callbackRequestTracker.scheduleRequest(impressionUrl)
  }

  override fun clickthrough() {
    callbackRequestTracker.scheduleRequest(clickthroughUrl)
  }
}
