package com.qubit.android.sdk.internal.placement.interactor

import com.qubit.android.sdk.api.placement.PlacementMode
import com.qubit.android.sdk.api.placement.PlacementPreviewOptions
import com.qubit.android.sdk.api.tracker.OnPlacementError
import com.qubit.android.sdk.api.tracker.OnPlacementSuccess
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationModel
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationRepository
import com.qubit.android.sdk.internal.placement.PlacementImpl
import com.qubit.android.sdk.internal.placement.callback.PlacementCallbackAPI
import com.qubit.android.sdk.internal.placement.callback.PlacementCallbackConnectorImpl
import com.qubit.android.sdk.internal.placement.connector.PlacementConnector
import com.qubit.android.sdk.internal.placement.model.PlacementModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class PlacementInteractorImpl(
    private val placementConnector: PlacementConnector,
    private val configurationRepository: ConfigurationRepository,
    private val deviceId: String
) : PlacementInteractor {

  companion object {
    private val DEFAULT_PLACEMENT_MODE = PlacementMode.LIVE
    private const val BASE_URL_PLACEHOLDER = "http://localhost/"
  }

  private val callbackApi = Retrofit.Builder()
      .baseUrl(BASE_URL_PLACEHOLDER)  // https://stackoverflow.com/questions/34842390/how-to-setup-retrofit-with-no-baseurl
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PlacementCallbackAPI::class.java)

  override fun fetchPlacement(
      placementId: String,
      mode: PlacementMode?,
      previewOptions: PlacementPreviewOptions,
      onSuccess: OnPlacementSuccess,
      onError: OnPlacementError
  ) {
    placementConnector.getPlacementModel(
        getPlacementApiHost(configurationRepository),
        placementId,
        mode ?: DEFAULT_PLACEMENT_MODE,
        deviceId,
        previewOptions,
        { handleSuccessfulResponse(it, onSuccess, onError) },
        { onError(it) }
    )
  }

  private fun getPlacementApiHost(configurationRepository: ConfigurationRepository): String {
    return configurationRepository.load().placementApiHost
        ?: ConfigurationModel.getDefault().placementApiHost
  }

  private fun handleSuccessfulResponse(
      placementModel: PlacementModel,
      onSuccess: OnPlacementSuccess,
      onError: OnPlacementError
  ) {
    val placementContent = placementModel.data?.placementContent
    if (placementContent != null) {
      try {
        val callbackConnector = PlacementCallbackConnectorImpl(
            callbackApi,
            placementContent.callbacks.impression,
            placementContent.callbacks.clickthrough
        )
        val placement = PlacementImpl(placementContent.content, callbackConnector)
        onSuccess(placement)
      } catch (e: Exception) {
        onError(e)
      }
    } else {
      onSuccess(null)
    }
  }
}
