package com.qubit.android.sdk.internal.experience.repository

import android.content.Context
import com.google.gson.Gson
import com.qubit.android.sdk.internal.common.logging.QBLogger

class ExperienceRepositoryImpl(private val appContext: Context) : ExperienceRepository {

  private val LOGGER = QBLogger.getFor("ExperienceRepository")

  private val PREFERENCES_FILE = "qubit_lookup"
  private val LOOKUP_KEY = "lookup"

  private val gson: Gson by lazy { Gson() }

  override fun save(experienceCache: ExperienceCache) {
//    val sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
//    sharedPreferences.edit()
//        .putString(LOOKUP_KEY, gson.toJson(lookupCache))
//        .commit()
  }

  override fun load(): ExperienceCache {
    return ExperienceCache()
  }
}