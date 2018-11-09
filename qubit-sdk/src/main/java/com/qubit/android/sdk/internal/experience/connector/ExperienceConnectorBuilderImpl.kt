package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.common.util.UrlUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class ExperienceConnectorBuilderImpl(
    private val trackingId: String,
    private val contextId: String
) : ExperienceConnectorBuilder {

  override fun buildFor(endpointUrl: String): ExperienceConnector =
      ExperienceConnectorImpl(
          trackingId,
          contextId,
          createConnector(endpointUrl)
      )
}

private fun createConnector(endpointUrl: String): ExperienceAPI {
  val retrofit = Retrofit.Builder()
      .baseUrl(UrlUtils.addProtocol(endpointUrl, true))
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  return retrofit.create(ExperienceAPI::class.java)
}