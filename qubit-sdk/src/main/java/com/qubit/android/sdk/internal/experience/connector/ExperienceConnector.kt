package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.experience.model.ExperienceModel

typealias OnResponseSuccess = (experienceModel: ExperienceModel) -> Unit
typealias OnResponseFailure = (throwable: Throwable) -> Unit

interface ExperienceConnector {
  fun getExperienceModel(
      experienceIds: String,
      variation: Int? = null,
      preview: Boolean? = null,
      ignoreSegments: Boolean? = null,
      onResponseSuccess: OnResponseSuccess,
      onResponseFailure: OnResponseFailure
  )

  fun getExperienceModel(): ExperienceModel?
}