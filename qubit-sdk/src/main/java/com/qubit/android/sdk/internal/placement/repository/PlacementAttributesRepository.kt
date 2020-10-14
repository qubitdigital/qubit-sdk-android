package com.qubit.android.sdk.internal.placement.repository

interface PlacementAttributesRepository {

  fun save(key: String, value: String)

  fun load(): MutableMap<String, *>
}
