package com.qubit.android.sdk.internal.experience.interactor

import com.qubit.android.sdk.api.tracker.OnExperienceError
import com.qubit.android.sdk.api.tracker.OnExperienceSuccess
import com.qubit.android.sdk.internal.common.logging.QBLogger
import com.qubit.android.sdk.internal.configuration.Configuration
import com.qubit.android.sdk.internal.configuration.connector.ConfigurationConnector
import com.qubit.android.sdk.internal.experience.ExperienceObject
import com.qubit.android.sdk.internal.experience.ExperienceService
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnectorBuilder

internal class ExperienceInteractorImpl(
    private val experienceConnectorBuilder: ExperienceConnectorBuilder,
    private val configurationConnector: ConfigurationConnector,
    private val experienceService: ExperienceService
) : ExperienceInteractor {

  companion object {
    private const val INTERACTOR_NAME = "ExperienceInteractor"

    @JvmStatic
    private val LOGGER = QBLogger.getFor(INTERACTOR_NAME)
  }

  override fun fetchExperience(
      onSuccess: OnExperienceSuccess,
      onError: OnExperienceError,
      experienceIdList: List<String>,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?
  ) {

    if (variation != null || preview != null || ignoreSegments != null) {
      getExperience(onSuccess, onError, experienceIdList, variation, preview, ignoreSegments)
    } else {
      experienceService.experienceData?.let {
        onSuccess(ExperienceObject(it))
      } ?: getExperience(onSuccess, onError, experienceIdList, variation, preview, ignoreSegments)
    }
  }

  private fun getExperience(
      onSuccess: OnExperienceSuccess,
      onError: OnExperienceError,
      experienceIdList: List<String>,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?) {

    val experiencesIds = experienceIdList.reduce { acc: String, s: String -> "$acc,$s" }

    experienceConnectorBuilder.buildFor(getExperienceApi()).getExperienceModel(
        experiencesIds,
        variation,
        preview,
        ignoreSegments,
        { onSuccess(ExperienceObject(it)) },
        { onError(it) }
    )
  }

  private fun getExperienceApi(): String =
      experienceService.configuration?.experienceApiHost?.let {
        it
      } ?: downloadConfiguration()

  private fun downloadConfiguration(): String {
    return configurationConnector.download().configuration.experienceApiHost //TODO rewrite to async
  }
}
