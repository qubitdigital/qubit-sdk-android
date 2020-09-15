package com.qubit.android.sdk.internal.placement

import com.google.gson.JsonObject
import com.qubit.android.sdk.api.placement.Placement
import com.qubit.android.sdk.internal.placement.callback.PlacementCallbackConnector

internal class PlacementImpl(
    override val content: JsonObject,
    private val callbackConnector: PlacementCallbackConnector
) : PlacementCallbackConnector by callbackConnector, Placement
