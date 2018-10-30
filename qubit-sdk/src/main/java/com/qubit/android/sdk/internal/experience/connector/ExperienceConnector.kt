package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import retrofit2.Callback

interface ExperienceConnector {
  fun getExperienceModel(callback: Callback<ExperienceModel>)
}