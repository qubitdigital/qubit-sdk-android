package com.qubit.android.sdk.internal.placement.connector

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlacementConnectorBuilderImpl : PlacementConnectorBuilder {

  companion object {
    private const val BASE_URL_PLACEHOLDER = "http://localhost/"
  }

  override fun build(): PlacementConnector {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_PLACEHOLDER)  // https://stackoverflow.com/questions/34842390/how-to-setup-retrofit-with-no-baseurl
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val placementAPI = retrofit.create(PlacementAPI::class.java)

    return PlacementConnectorImpl(placementAPI)
  }
}
