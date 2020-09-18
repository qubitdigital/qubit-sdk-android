package com.qubit.android.sdk.internal.placement.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.qubit.android.sdk.internal.common.logging.QBLogger
import com.qubit.android.sdk.internal.placement.model.PlacementModel

internal class PlacementRepositoryImpl(
    appContext: Context
) : PlacementRepository {

  companion object {
    @JvmStatic
    private val LOGGER = QBLogger.getFor("PlacementRepository")
    private const val PREFERENCES_FILE = "qubit_placement"
  }

  private val sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
  private val gson: Gson by lazy { Gson() }

  override fun save(key: String, placement: PlacementModel) {
    sharedPreferences.edit()
        .putString(key, gson.toJson(placement))
        .apply()
  }

  override fun load(key: String): PlacementModel? {
    return try {
      val placementJson = sharedPreferences.getString(key, null)
      placementJson?.let { gson.fromJson(it, PlacementModel::class.java) }
    } catch (e: JsonSyntaxException) {
      LOGGER.e("Error parsing lookup data JSON from local storage.", e)
      null
    }
  }
}
