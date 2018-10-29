package com.qubit.android.sdk.internal.experience

internal interface ExperienceService {

  fun registerExperienceListener(listener: ExperienceListener)
  fun unregisterLookupListener(listener: ExperienceListener)

  interface ExperienceListener {
    fun onExperienceDataChange(experienceData: ExperienceData)
  }
}