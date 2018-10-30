package com.qubit.android.sdk.internal.experience.model

import org.json.JSONObject

data class ExperiencePayload(
    val payload: JSONObject,
    val isControl: Boolean,
    val id: Int,
    val callback: String,
    val variation: Int
)