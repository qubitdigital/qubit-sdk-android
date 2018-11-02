package com.qubit.android.sdk.internal.experience.connector

interface ExperienceConnectorBuilder {
  fun buildFor(endpointUrl: String): ExperienceConnector
}