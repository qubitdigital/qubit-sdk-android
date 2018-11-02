package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import retrofit2.Callback

interface ExperienceConnector {
  fun getExperienceModel(
      experienceIds: String,
      variation: Int? = null,
      preview: Boolean? = null,
      ignoreSegments: Boolean? = null,
      onResponseSuccess: (experienceModel: ExperienceModel) -> Unit,
      onResponseFailure: (throwable: Throwable) -> Unit
  )

  fun getExperienceModel(): ExperienceModel?
}