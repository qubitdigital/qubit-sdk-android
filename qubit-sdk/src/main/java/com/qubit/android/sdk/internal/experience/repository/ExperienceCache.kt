package com.qubit.android.sdk.internal.experience.repository

import com.qubit.android.sdk.internal.experience.model.ExperienceModel

data class ExperienceCache(
    var experienceModel: ExperienceModel,
    var lastUpdateTimestamp: Long
)