package com.qubit.android.sdk.internal.placement.model

import com.google.gson.JsonObject

data class PlacementContentModel(
    val content: JsonObject,
    val callbacks: PlacementCallbacksModel
)
