package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.experience.callback.ExperienceCallbackConnector
import com.qubit.android.sdk.internal.experience.model.ExperiencePayload

internal class ExperienceImpl(
    override val experiencePayload: ExperiencePayload,
    private val callbackConnector: ExperienceCallbackConnector
) : ExperienceCallbackConnector by callbackConnector, Experience
