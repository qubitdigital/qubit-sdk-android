package com.qubit.android.sdk.internal.experience

internal interface ExperienceInteractor {
  fun fetchExperience(
      experienceIdList: List<String>,
      variation: Int?,
      preview: Boolean?,
      ignoreSegments: Boolean?
  )
}