package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.experience.callback.ExperienceCallbackConnector
import com.qubit.android.sdk.internal.experience.model.ExperiencePayload

interface Experience : ExperienceCallbackConnector {
  val experiencePayload: ExperiencePayload
}
