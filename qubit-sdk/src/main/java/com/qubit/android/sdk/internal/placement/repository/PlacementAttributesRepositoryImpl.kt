package com.qubit.android.sdk.internal.placement.repository

import android.content.Context
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class PlacementAttributesRepositoryImpl(
    appContext: Context
) : PlacementAttributesRepository {

  companion object {
    private const val PREFERENCES_FILE = "qubit_placement_query_attributes"
  }

  private val sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
  private val jsonParser = JsonParser()

  override fun save(key: String, value: String) {
    sharedPreferences.edit()
        .putString(key, value)
        .apply()
  }

  override fun load(): Map<String, JsonObject> {
    val result = HashMap<String, JsonObject>()
    val storedItems = sharedPreferences.all
    for (key in storedItems.keys) {
      getJsonObject(storedItems[key])?.let {
        result.put(key, it)
      }
    }
    return result
  }

  private fun getJsonObject(value: Any?): JsonObject? = try {
    jsonParser.parse(value.toString()).asJsonObject
  } catch (e: Exception) {
    null
  }
}
