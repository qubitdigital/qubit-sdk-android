package com.qubit.android.sdk.internal.placement.callback

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Url

interface PlacementCallbackAPI {

  @POST
  fun postCallbackRequest(@Url url: String): Call<Void>
}
