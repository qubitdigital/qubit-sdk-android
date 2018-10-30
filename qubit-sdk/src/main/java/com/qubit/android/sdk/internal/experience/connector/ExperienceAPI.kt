package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExperienceAPI {

  companion object {
    private const val TRACKING_ID_PATH = "trackingId"
  }

  @GET("v1/{$TRACKING_ID_PATH}/experiences")
  fun getExperience(
      @Path(TRACKING_ID_PATH) trackingId: String,
      @Query("contextId") contextId: String,
      @Query("experienceIds") experienceIds: String,
      @Query("variation") variation: Int?,
      @Query("preview") preview: Boolean?,
      @Query("ignoreSegments") ignoreSegments: Boolean?
  ): Call<ExperienceModel>
}