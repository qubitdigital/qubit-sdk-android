package com.qubit.android.sdk.internal.experience.interactor

import com.qubit.android.sdk.api.tracker.OnExperienceError
import com.qubit.android.sdk.api.tracker.OnExperienceSuccess
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationModel
import com.qubit.android.sdk.internal.experience.Experience
import com.qubit.android.sdk.internal.experience.ExperienceImpl
import com.qubit.android.sdk.internal.experience.callback.ExperienceCallbackConnectorImpl
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnectorBuilder
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import com.qubit.android.sdk.internal.experience.service.ExperienceService

internal class ExperienceInteractorImpl(
    private val experienceConnectorBuilder: ExperienceConnectorBuilder,
    private val experienceService: ExperienceService,
    private val deviceId: String
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

    experienceConnectorBuilder.buildFor(getExperienceApi()).getExperienceModel(
        { onSuccess(mapExperienceData(experienceIdList, it)) },
        { onError(it) },
        variation = variation,
        preview = preview,
        ignoreSegments = ignoreSegments
    )
  }

  private fun mapExperienceData(experienceIdList: List<Int>, experienceModel: ExperienceModel): List<Experience> =
      experienceModel.experiencePayloads
          ?.filter { experienceIdList.isEmpty() || experienceIdList.contains(it.id) }
          ?.map {
            val callbackConnector = ExperienceCallbackConnectorImpl(it.callback, deviceId)
            ExperienceImpl(it, callbackConnector)
          }
          ?: emptyList()

  private fun getExperienceApi(): String =
      experienceService.configuration?.experienceApiHost ?: getDefaultExperienceApiHost()

  private fun getDefaultExperienceApiHost() = ConfigurationModel.getDefault().experienceApiHost
}
