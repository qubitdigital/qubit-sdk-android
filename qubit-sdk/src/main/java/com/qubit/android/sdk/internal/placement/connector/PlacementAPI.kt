package com.qubit.android.sdk.internal.placement.connector

import com.qubit.android.sdk.internal.placement.model.PlacementModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface PlacementAPI {

  @POST
  fun getPlacement(
      @Url url: String,
      @Body body: PlacementRequestRestModel
  ): Call<PlacementModel>
}
