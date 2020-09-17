package com.qubit.android.sdk.internal.placement.callback

import com.qubit.android.sdk.api.placement.PlacementCallbackConnector
import com.qubit.android.sdk.internal.common.logging.QBLogger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Url

internal class PlacementCallbackConnectorImpl(
    private val impressionUrl: String,
    private val clickthroughUrl: String
) : PlacementCallbackConnector {

  companion object {
    @JvmStatic
    private val LOGGER = QBLogger.getFor("PlacementCallbackConnector")

    private const val BASE_URL_PLACEHOLDER = "http://localhost/"
  }

  private val callbackApi = Retrofit.Builder()
      .baseUrl(BASE_URL_PLACEHOLDER)  // https://stackoverflow.com/questions/34842390/how-to-setup-retrofit-with-no-baseurl
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(CallbackAPI::class.java)

  override fun impression() {
    postRequest(impressionUrl)
  }

  override fun clickthrough() {
    postRequest(clickthroughUrl)
  }

  private fun postRequest(url: String) {
    callbackApi.postCallbackRequest(url).enqueue(LoggingCallback(url))
  }

  private interface CallbackAPI {
    @POST
    fun postCallbackRequest(@Url url: String): Call<Void>
  }

  private class LoggingCallback(
      private val url: String
  ) : Callback<Void> {

    override fun onResponse(call: Call<Void>, response: Response<Void>) {
      LOGGER.d("Successfully triggered placement callback to $url")
    }

    override fun onFailure(call: Call<Void>, t: Throwable) {
      LOGGER.e("Failed to send placement callback to $url", t)
    }
  }
}
