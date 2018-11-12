package com.qubit.android.sdk.internal.experience.model

import com.google.gson.JsonObject

data class ExperiencePayload(
    val payload: JsonObject,
    val isControl: Boolean,
    val id: Int,
    val callback: String,
    val variation: Int
)