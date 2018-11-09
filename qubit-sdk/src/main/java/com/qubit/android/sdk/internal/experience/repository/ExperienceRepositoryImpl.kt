package com.qubit.android.sdk.internal.experience.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.qubit.android.sdk.internal.common.logging.QBLogger

internal class ExperienceRepositoryImpl(private val appContext: Context) : ExperienceRepository {

  companion object {
    @JvmStatic
    private val LOGGER = QBLogger.getFor("ExperienceRepository")

    private const val PREFERENCES_FILE = "qubit_experience"
    private const val EXPERIENCE_KEY = "experience"
  }

  private val gson: Gson by lazy { Gson() }

  override fun save(experienceCache: ExperienceCache) {
    val sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
    sharedPreferences.edit()
        .putString(EXPERIENCE_KEY, gson.toJson(experienceCache))
        .apply()
  }

  override fun load(): ExperienceCache? {
    return try {
      val sharedPref = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
      val experienceCacheJson = sharedPref.getString(EXPERIENCE_KEY, null)
      experienceCacheJson?.let { gson.fromJson(it, ExperienceCache::class.java) }
    } catch (e: JsonSyntaxException) {
      LOGGER.e("Error parsing lookup data JSON from local storage.", e)
      null
    }

  }
}