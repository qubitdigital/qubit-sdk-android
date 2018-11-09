package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface ExperienceAPI {

  companion object {
    private const val TRACKING_ID_PATH = "trackingId"
  }

  @GET("v1/{$TRACKING_ID_PATH}/experiences")
  fun getExperience(
      @Path(TRACKING_ID_PATH) trackingId: String,
      @Query("contextId") contextId: String,
      @Query("experienceIds") experienceIds: String? = null,
      @Query("variation") variation: Int? = null,
      @Query("preview") preview: Boolean? = null,
      @Query("ignoreSegments") ignoreSegments: Boolean? = null
  ): Call<ExperienceModel>
}