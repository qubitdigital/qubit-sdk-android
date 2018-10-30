package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.common.logging.QBLogger
import com.qubit.android.sdk.internal.configuration.Configuration
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnectorBuilder
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import com.qubit.android.sdk.internal.experience.repository.ExperienceRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

internal class ExperienceInteractorImpl(
    private val configuration: Configuration,
    private val experienceRepository: ExperienceRepository,
    private val experienceConnectorBuilder: ExperienceConnectorBuilder
) : ExperienceInteractor {

  companion object {
    private const val INTERACTOR_NAME = "ExperienceInteractor"

    @JvmStatic
    private val LOGGER = QBLogger.getFor(INTERACTOR_NAME)
  }

  override fun fetchExperience(
      experienceIdList: List<String>,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?,
      experienceListener: ExperienceListener
  ) {

    //TODO check if is in database

    experienceConnectorBuilder.buildFor(
        configuration.experienceApiHost,
        experienceIdList,
        variation,
        preview,
        ignoreSegments
    ).getExperienceModel(object : Callback<ExperienceModel> {
      override fun onFailure(call: Call<ExperienceModel>, throwable: Throwable) {
        when (throwable) {
          is IOException -> LOGGER.e("Error connecting to server.", throwable)
          is RuntimeException -> LOGGER.e("Unexpected exception while getting lookup.", throwable)
        }

        experienceListener.onError()
      }

      override fun onResponse(call: Call<ExperienceModel>, response: Response<ExperienceModel>) {
        response.body()?.let {
              experienceListener.onExperienceReceive(ExperienceObject(it))
        } ?: LOGGER.e("Response doesn't contain body.")
      }
    })
  }
}
