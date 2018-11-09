package com.qubit.android.sdk.internal.experience.interactor

import com.qubit.android.sdk.api.tracker.OnExperienceError
import com.qubit.android.sdk.api.tracker.OnExperienceSuccess
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationModel
import com.qubit.android.sdk.internal.experience.Experience
import com.qubit.android.sdk.internal.experience.ExperienceImpl
import com.qubit.android.sdk.internal.experience.callback.CallbackConnector
import com.qubit.android.sdk.internal.experience.callback.CallbackConnectorImpl
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnectorBuilder
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import com.qubit.android.sdk.internal.experience.service.ExperienceService

internal class ExperienceInteractorImpl(
    private val experienceConnectorBuilder: ExperienceConnectorBuilder,
    private val experienceService: ExperienceService
) : ExperienceInteractor {

  override fun fetchExperience(
      onSuccess: OnExperienceSuccess,
      onError: OnExperienceError,
      experienceIdList: List<Int>,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?
  ) {

    if (variation != null || preview != null || ignoreSegments != null) {
      getExperience(onSuccess, onError, experienceIdList, variation, preview, ignoreSegments)
    } else {
      experienceService.experienceData?.let {
        onSuccess(mapExperienceData(experienceIdList, it))
      } ?: getExperience(onSuccess, onError, experienceIdList, variation, preview, ignoreSegments)
    }
  }

  private fun getExperience(
      onSuccess: OnExperienceSuccess,
      onError: OnExperienceError,
      experienceIdList: List<Int>,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?) {

    val experiencesIds = experienceIdList
        .ifEmpty { listOf("") }
        .map { it.toString() }
        .reduce { acc: String, s: String -> "$acc,$s" }

    experienceConnectorBuilder.buildFor(getExperienceApi()).getExperienceModel(
        experiencesIds,
        variation,
        preview,
        ignoreSegments,
        { onSuccess(mapExperienceData(experienceIdList, it)) },
        { onError(it) }
    )
  }

  private fun mapExperienceData(experienceIdList: List<Int>, experienceModel: ExperienceModel): List<Experience> =
      experienceModel.experiencePayloads
          .filter { experienceIdList.isEmpty() || experienceIdList.contains(it.id) }
          .map {
            val callbackConnector: CallbackConnector = CallbackConnectorImpl(it.callback, it.id)
            ExperienceImpl(it, callbackConnector)
          }

  private fun getExperienceApi(): String =
      experienceService.configuration?.experienceApiHost ?: getDefaultExperienceApiHost()

  private fun getDefaultExperienceApiHost() = ConfigurationModel.getDefault().experienceApiHost
}
