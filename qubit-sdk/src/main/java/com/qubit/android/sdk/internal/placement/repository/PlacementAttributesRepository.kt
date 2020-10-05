package com.qubit.android.sdk.internal.placement.repository

import com.google.gson.JsonObject

interface PlacementAttributesRepository {

  fun save(key: String, value: String)

  fun load(): Map<String, JsonObject>
}
