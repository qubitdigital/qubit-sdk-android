package com.qubit.android.sdk.internal.placement.connector

import com.google.gson.JsonObject

data class PlacementRequestVariablesRestModel(
    val mode: String,
    val placementId: String,
    val attributes: JsonObject,
    val previewOptions: PlacementRequestPreviewOptionsRestModel,
    val resolveVisitorState: Boolean
)
