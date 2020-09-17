package com.qubit.android.sdk.internal.placement.callback

import com.qubit.android.sdk.api.placement.PlacementCallbackConnector
import com.qubit.android.sdk.internal.common.logging.QBLogger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class PlacementCallbackConnectorImpl(
    private val callbackApi: PlacementCallbackAPI,
    private val impressionUrl: String,
    private val clickthroughUrl: String
) : PlacementCallbackConnector {

  companion object {
    @JvmStatic
    private val LOGGER = QBLogger.getFor("PlacementCallbackConnector")
  }

  override fun impression() {
    postRequest(impressionUrl)
  }

  override fun clickthrough() {
    postRequest(clickthroughUrl)
  }

  private fun postRequest(url: String) {
    callbackApi.postCallbackRequest(url).enqueue(LoggingCallback(url))
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
