package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.common.logging.QBLogger
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import java.io.IOException

class ExperienceConnectorImpl(
    private val trackingId: String,
    private val contextId: String,
    private val experienceIds: String,
    private val variation: Int?,
    private val preview: Boolean?,
    private val ignoreSegments: Boolean?,
    private val experienceAPI: ExperienceAPI
) : ExperienceConnector {

  companion object {
    @JvmStatic
    private val LOGGER = QBLogger.getFor("ExperienceConnector")
  }

  override fun getExperienceModel(): ExperienceModel? {
    try {
      val response = experienceAPI.getExperience(
          trackingId,
          contextId,
          experienceIds,
          variation,
          preview,
          ignoreSegments
      ).execute()

      val responseBody = response.body()
      if (responseBody == null) {
        LOGGER.e("Response doesn't contain body.")
      }
      return responseBody
    } catch (e: IOException) {
      LOGGER.e("Error connecting to server.", e)
      return null
    } catch (e: RuntimeException) {
      LOGGER.e("Unexpected exception while getting lookup.", e)
      return null
    }
  }
}