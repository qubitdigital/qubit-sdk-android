package com.qubit.android.sdk.internal.placement.connector

data class PlacementRequestRestModel(
    val query: String,
    val variables: PlacementRequestVariablesRestModel
)
