package com.qubit.android.sdk.internal.placement.model

import com.google.gson.JsonElement

data class PlacementContentModel(
    // Every access to 'content' field should contain null check as null value (allowed here) won't be casted to JsonNull as expected but to null (although
    // it is non-nullable) : https://stackoverflow.com/questions/52837665/why-kotlin-data-classes-can-have-nulls-in-non-nullable-fields-with-gson
    val content: JsonElement,
    val callbacks: PlacementCallbacksModel
)
