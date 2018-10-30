package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import retrofit2.Callback

class ExperienceConnectorImpl(
    private val trackingId: String,
    private val contextId: String,
    private val experienceIds: String,
    private val variation: Int?,
    private val preview: Boolean?,
    private val ignoreSegments: Boolean?,
    private val experienceAPI: ExperienceAPI
) : ExperienceConnector {

  override fun getExperienceModel(callback: Callback<ExperienceModel>) {
    experienceAPI.getExperience(
        trackingId,
        contextId,
        experienceIds,
        variation,
        preview,
        ignoreSegments
    ).enqueue(callback)
  }
}
