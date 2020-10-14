package com.qubit.android.sdk.internal.placement.repository

import android.content.Context

class PlacementAttributesRepositoryImpl(
    appContext: Context
) : PlacementAttributesRepository {

  companion object {
    private const val PREFERENCES_FILE = "qubit_placement_query_attributes"
  }

  private val sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

  override fun save(key: String, value: String) {
    sharedPreferences.edit()
        .putString(key, value)
        .apply()
  }

  override fun load(): MutableMap<String, *> = sharedPreferences.all
}
