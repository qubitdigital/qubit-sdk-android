package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.experience.model.ExperiencePayload
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface Experience {
  val experiencePayload: ExperiencePayload
  fun shown()
}


interface CallbackConnector {
  fun shown()
}

class CallbackConnectorImpl(private val callback: String, private val id: String) : CallbackConnector {

  override fun shown() {
    val api = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CallbackAPI::class.java)

    api.makeCallback(callback, id).enqueue(null)
  }

  private interface CallbackAPI {
    @POST
    fun makeCallback(@Url url: String, @Body id: String): Call<Void>
  }
}

class ExperienceObjectImpl(
    override val experiencePayload: ExperiencePayload,
    private val callbackConnector: CallbackConnectorImpl
) : CallbackConnector by callbackConnector, Experience