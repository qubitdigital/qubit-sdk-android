package com.qubit.android.sdk.internal.callbacktracker.repository

interface CallbackRequestRepository {

  fun init()

  fun insert(url: String)

  fun fetchFirst(): String?

  fun size(): Int
}
