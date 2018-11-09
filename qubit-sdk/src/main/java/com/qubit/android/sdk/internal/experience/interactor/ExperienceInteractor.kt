package com.qubit.android.sdk.internal.experience.interactor

import com.qubit.android.sdk.api.tracker.OnExperienceError
import com.qubit.android.sdk.api.tracker.OnExperienceSuccess

internal interface ExperienceInteractor {
  fun fetchExperience(
      onSuccess: OnExperienceSuccess,
      onError: OnExperienceError,
      experienceIdList: List<Int>,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?
  )
}