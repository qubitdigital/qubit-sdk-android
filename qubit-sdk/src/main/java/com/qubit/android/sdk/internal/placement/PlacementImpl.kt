package com.qubit.android.sdk.internal.placement

import com.google.gson.JsonObject
import com.qubit.android.sdk.api.placement.Placement
import com.qubit.android.sdk.api.placement.PlacementCallbackConnector

internal class PlacementImpl(
    override val content: JsonObject,
    override val impressionUrl: String,
    override val clickthroughUrl: String,
    private val callbackConnector: PlacementCallbackConnector
) : PlacementCallbackConnector by callbackConnector, Placement
