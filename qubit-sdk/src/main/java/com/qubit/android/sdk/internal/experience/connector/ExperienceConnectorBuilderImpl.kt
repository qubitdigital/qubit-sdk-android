package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.common.util.UrlUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExperienceConnectorBuilderImpl(val trackingId: String, val contextId: String) : ExperienceConnectorBuilder {

  override fun buildFor(
      endpointUrl: String,
      experienceIds: List<String>,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?
  ): ExperienceConnector {

    val experiences = experienceIds.foldRight("") { acc: String, s: String -> "$acc.$s" }

    return ExperienceConnectorImpl(
        trackingId,
        contextId,
        experiences,
        variation,
        preview,
        ignoreSegments,
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
}