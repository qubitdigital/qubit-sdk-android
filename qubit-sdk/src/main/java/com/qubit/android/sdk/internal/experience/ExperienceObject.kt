package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import com.qubit.android.sdk.internal.experience.model.ExperiencePayload
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url


class ExperienceObject(val experiencePayload: ExperiencePayload) {

  fun shown() {
    val api = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CallbackAPI::class.java)

    api.makeCallback(experiencePayload.callback)
  }

  interface CallbackAPI {
    @POST
    fun makeCallback(@Url url: String): Call<Void>
  }
}