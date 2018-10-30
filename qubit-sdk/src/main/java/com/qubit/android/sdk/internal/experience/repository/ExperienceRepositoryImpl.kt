package com.qubit.android.sdk.internal.experience.repository

import android.content.Context

class ExperienceRepositoryImpl() : ExperienceRepository {

  override fun save(experienceCache: ExperienceCache) {

  }

  override fun load(): ExperienceCache {
    return ExperienceCache()
  }
}