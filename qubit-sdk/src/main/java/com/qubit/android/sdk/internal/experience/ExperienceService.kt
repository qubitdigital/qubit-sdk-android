package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.configuration.Configuration
import com.qubit.android.sdk.internal.experience.model.ExperienceModel

interface ExperienceService {
  val experienceData: ExperienceModel?
  val configuration: Configuration?
}