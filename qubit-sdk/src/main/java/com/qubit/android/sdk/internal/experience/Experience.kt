package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.experience.callback.CallbackConnector
import com.qubit.android.sdk.internal.experience.model.ExperiencePayload

interface Experience : CallbackConnector {
  val experiencePayload: ExperiencePayload
}