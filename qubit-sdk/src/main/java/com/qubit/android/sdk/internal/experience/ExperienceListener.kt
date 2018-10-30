package com.qubit.android.sdk.internal.experience

interface ExperienceListener {
  fun onExperienceReceive(experienceObject: ExperienceObject)
  fun onError()
}