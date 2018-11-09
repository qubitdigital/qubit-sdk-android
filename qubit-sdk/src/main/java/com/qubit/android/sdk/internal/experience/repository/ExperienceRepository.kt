package com.qubit.android.sdk.internal.experience.repository

internal interface ExperienceRepository {
  fun save(experienceCache: ExperienceCache)
  fun load(): ExperienceCache?
}