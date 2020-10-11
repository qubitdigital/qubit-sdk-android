package com.qubit.android.sdk.internal.callbacktracker.repository

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("ApplySharedPref")
internal class CallbackRequestRepositoryImpl(
    appContext: Context
) : CallbackRequestRepository {

  companion object {
    private const val PREFERENCES_FILE = "qubit_callback_requests"
    private const val DEFAULT_KEY_VALUE = 1L
  }

  private val sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
  private var currentKey = DEFAULT_KEY_VALUE

  override fun init() {
    val lastKey = getLastKey()
    currentKey = lastKey ?: DEFAULT_KEY_VALUE
  }

  @Synchronized
  override fun insert(url: String) {
    currentKey++
    sharedPreferences.edit()
        .putString(currentKey.toString(), url)
        .commit()
  }

  @Synchronized
  override fun fetchFirst(): String? {
    val key = getFirstKey()?.toString()
    return if (key != null) {
      val url = sharedPreferences.getString(key, null)
      if (url != null) {
        remove(key)
        url
      } else {
        null
      }
    } else {
      null
    }
  }

  private fun remove(key: String) {
    sharedPreferences.edit()
        .remove(key)
        .commit()
  }

  @Synchronized
  override fun size() = sharedPreferences.all.size

  private fun getFirstKey() = sharedPreferences.all.keys.mapNotNull { it.toLongOrNull() }.min()

  private fun getLastKey() = sharedPreferences.all.keys.mapNotNull { it.toLongOrNull() }.max()
}
