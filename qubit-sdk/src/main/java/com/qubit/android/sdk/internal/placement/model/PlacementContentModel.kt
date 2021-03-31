package com.qubit.android.sdk.internal.placement.model

import com.google.gson.JsonElement

data class PlacementContentModel(
    val content: JsonElement,
    val callbacks: PlacementCallbacksModel
)
