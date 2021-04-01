package com.qubit.android.sdk.internal.callbacktracker

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Url

interface CallbackRequestAPI {

  @POST
  fun postCallbackRequest(@Url url: String): Call<Void>
}
