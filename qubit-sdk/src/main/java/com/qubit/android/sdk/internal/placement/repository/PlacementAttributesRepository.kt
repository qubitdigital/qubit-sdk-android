package com.qubit.android.sdk.internal.placement.repository

import com.google.gson.JsonObject

interface PlacementAttributesRepository {

  fun save(key: String, value: JsonObject)

  fun load(): MutableMap<String, JsonObject>
}
