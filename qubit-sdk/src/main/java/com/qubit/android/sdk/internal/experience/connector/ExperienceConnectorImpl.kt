package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.common.logging.QBLogger
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

internal class ExperienceConnectorImpl(
    private val trackingId: String,
    private val contextId: String,
    private val experienceAPI: ExperienceAPI
) : ExperienceConnector {

  companion object {
    @JvmStatic
    private val LOGGER = QBLogger.getFor("ExperienceConnector")
  }

  override fun getExperienceModel(
      onResponseSuccess: OnResponseSuccess,
      onResponseFailure: OnResponseFailure,
      experienceIds: String?,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?
  ) {

    experienceAPI.getExperience(
        trackingId,
        contextId,
        experienceIds,
        variation,
        preview,
        ignoreSegments
    ).enqueue(object : Callback<ExperienceModel> {
      override fun onResponse(call: Call<ExperienceModel>, response: Response<ExperienceModel>) {
        response.body()?.let {
          onResponseSuccess(it)
        } ?: onResponseFailure(Exception("Response doesn't contain body."))
      }

      override fun onFailure(call: Call<ExperienceModel>, throwable: Throwable) {
        when (throwable) {
          is IOException -> LOGGER.e("Error connecting to server.", throwable)
          is RuntimeException -> LOGGER.e("Unexpected exception while getting lookup.", throwable)
        }

        onResponseFailure(throwable)
      }
    })
  }

  override fun getExperienceModel(): ExperienceModel? {
    return try {
      val response = experienceAPI.getExperience(trackingId, contextId).execute()

      val responseBody = response.body()
      if (responseBody == null) {
        LOGGER.e("Response doesn't contain body.")
      }
      responseBody
    } catch (e: IOException) {
      LOGGER.e("Error connecting to server.", e)
      null
    } catch (e: RuntimeException) {
      LOGGER.e("Unexpected exception while getting lookup.", e)
      null
    }
  }
}
