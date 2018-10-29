package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.experience.model.ExperienceModel

interface ExperienceConnector {
  fun getExperienceModel(): ExperienceModel?
}