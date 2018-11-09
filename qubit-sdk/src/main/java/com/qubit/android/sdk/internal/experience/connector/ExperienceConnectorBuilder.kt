package com.qubit.android.sdk.internal.experience.connector

internal interface ExperienceConnectorBuilder {
  fun buildFor(endpointUrl: String): ExperienceConnector
}