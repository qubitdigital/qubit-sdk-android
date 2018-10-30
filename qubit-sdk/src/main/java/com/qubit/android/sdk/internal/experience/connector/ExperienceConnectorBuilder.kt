package com.qubit.android.sdk.internal.experience.connector

interface ExperienceConnectorBuilder {

  fun buildFor(endpointUrl: String,
               experienceIdList: List<String>,
               variation: Int?,
               preview: Boolean?,
               ignoreSegments: Boolean?
  ): ExperienceConnector
}