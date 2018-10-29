package com.qubit.android.sdk.internal.experience.repository

interface ExperienceRepository {
  fun save(experienceCache: ExperienceCache)
  fun load(): ExperienceCache
}