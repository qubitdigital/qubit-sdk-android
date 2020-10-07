package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonObject
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
import com.qubit.android.sdk.internal.placement.model.PlacementContentModel
import com.qubit.android.sdk.internal.placement.model.PlacementModel
import com.qubit.android.sdk.internal.placement.repository.PlacementRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class PlacementInteractorImpl(
    private val placementConnector: PlacementConnector,
    private val configurationRepository: ConfigurationRepository,
    private val placementRepository: PlacementRepository,
    private val placementQueryAttributesBuilder: PlacementQueryAttributesBuilder,
    private val deviceId: String
) : PlacementInteractor {

  companion object {
    private val DEFAULT_PLACEMENT_MODE = PlacementMode.LIVE
    private const val BASE_URL_PLACEHOLDER = "http://localhost/"
    private const val KEY_SEPARATOR = "|"
  }

  private val callbackApi = Retrofit.Builder()
      .baseUrl(BASE_URL_PLACEHOLDER)  // https://stackoverflow.com/questions/34842390/how-to-setup-retrofit-with-no-baseurl
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PlacementCallbackAPI::class.java)

  override fun fetchPlacement(
      placementId: String,
      mode: PlacementMode?,
      customAttributes: JsonObject?,
      previewOptions: PlacementPreviewOptions,
      onSuccess: OnPlacementSuccess,
      onError: OnPlacementError
  ) {
    val placementMode = mode ?: DEFAULT_PLACEMENT_MODE
    val attributes = placementQueryAttributesBuilder.buildJson(deviceId, customAttributes)
    val cacheKey = buildCacheKey(placementId, placementMode, previewOptions, attributes)
    placementConnector.getPlacementModel(
        getPlacementApiHost(configurationRepository),
        placementId,
        placementMode,
        previewOptions,
        attributes,
        { handleSuccessfulResponse(it, cacheKey, true, onSuccess) },
        { handleErrorResponse(it, cacheKey, onSuccess, onError) }
    )
  }

  private fun buildCacheKey(
      placementId: String,
      placementMode: PlacementMode,
      previewOptions: PlacementPreviewOptions,
      attributes: JsonObject
  ): String {
    val keyString = StringBuilder().apply {
      append(placementId)
      append(KEY_SEPARATOR)
      append(placementMode.name)
      append(KEY_SEPARATOR)
      append(previewOptions.campaignId)
      append(KEY_SEPARATOR)
      append(previewOptions.experienceId)
      append(KEY_SEPARATOR)
      append(attributes.toString())
    }.toString()
    return keyString.hashCode().toString()
  }

  private fun getPlacementApiHost(configurationRepository: ConfigurationRepository): String {
    return configurationRepository.load()?.placementApiHost
        ?: ConfigurationModel.getDefault().placementApiHost
  }

  private fun handleSuccessfulResponse(
      placementModel: PlacementModel,
      cacheKey: String,
      shouldUpdateCache: Boolean,
      onSuccess: OnPlacementSuccess
  ) {
    val placementContent = placementModel.data?.placementContent
    if (shouldUpdateCache) {
      placementRepository.save(cacheKey, placementModel)
    }
    onSuccess(buildPlacement(placementContent))
  }

  private fun buildPlacement(placementContent: PlacementContentModel?): PlacementImpl? {
    return if (placementContent != null) {
      val callbackConnector = PlacementCallbackConnectorImpl(
          callbackApi,
          placementContent.callbacks.impression,
          placementContent.callbacks.clickthrough
      )
      PlacementImpl(placementContent.content, callbackConnector)
    } else {
      null
    }
  }

  private fun handleErrorResponse(
      throwable: Throwable,
      cacheKey: String,
      onSuccess: OnPlacementSuccess,
      onError: OnPlacementError
  ) {
    placementRepository.load(cacheKey)?.let {
      handleSuccessfulResponse(it, cacheKey, false, onSuccess)
    } ?: onError(throwable)
  }
}
