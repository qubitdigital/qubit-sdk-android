package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.experience.callback.CallbackConnector
import com.qubit.android.sdk.internal.experience.model.ExperiencePayload

class ExperienceImpl(
    override val experiencePayload: ExperiencePayload,
    private val callbackConnector: CallbackConnector
) : CallbackConnector by callbackConnector, Experience