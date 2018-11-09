package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.experience.model.ExperienceModel

internal typealias OnResponseSuccess = (experienceModel: ExperienceModel) -> Unit
internal typealias OnResponseFailure = (throwable: Throwable) -> Unit

internal interface ExperienceConnector {
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