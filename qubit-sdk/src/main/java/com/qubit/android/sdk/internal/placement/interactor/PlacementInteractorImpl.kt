package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.qubit.android.sdk.api.placement.PlacementMode
import com.qubit.android.sdk.api.placement.PlacementPreviewOptions
import com.qubit.android.sdk.api.tracker.OnPlacementError
import com.qubit.android.sdk.api.tracker.OnPlacementSuccess
import com.qubit.android.sdk.internal.callbacktracker.CallbackRequestTracker
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationModel
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationRepository
import com.qubit.android.sdk.internal.placement.PlacementImpl
import com.qubit.android.sdk.internal.placement.callback.PlacementCallbackConnectorImpl
import com.qubit.android.sdk.internal.placement.connector.PlacementConnector
import com.qubit.android.sdk.internal.placement.model.PlacementContentModel
import com.qubit.android.sdk.internal.placement.model.PlacementModel
import com.qubit.android.sdk.internal.placement.repository.PlacementRepository

internal class PlacementInteractorImpl(
    private val placementConnector: PlacementConnector,
    private val callbackRequestTracker: CallbackRequestTracker,
    private val configurationRepository: ConfigurationRepository,
    private val placementRepository: PlacementRepository,
    private val placementQueryAttributesBuilder: PlacementQueryAttributesBuilder,
    private val placementAttributesInteractor: PlacementAttributesInteractor,
    private val deviceId: String
) : PlacementInteractor {

  companion object {
    private val DEFAULT_PLACEMENT_MODE = PlacementMode.LIVE
    private const val KEY_SEPARATOR = "|"
  }

  override fun fetchPlacement(
      placementId: String,
      mode: PlacementMode?,
      customAttributes: JsonObject?,
      previewOptions: PlacementPreviewOptions,
      onSuccess: OnPlacementSuccess,
      onError: OnPlacementError
  ) {
    val placementMode = mode ?: DEFAULT_PLACEMENT_MODE
    val cachedAttributes = placementAttributesInteractor.loadAttributesMap()
    val attributes = placementQueryAttributesBuilder.buildJson(deviceId, customAttributes, cachedAttributes)
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
      if (placementContent != null) {
        placementRepository.save(cacheKey, placementModel)
      } else {
        placementRepository.remove(cacheKey)
      }
    }
    onSuccess(buildPlacement(placementContent))
  }

  private fun buildPlacement(placementContent: PlacementContentModel?): PlacementImpl? {
    return if (placementContent != null) {
      val impressionUrl = placementContent.callbacks.impression ?: ""
      val clickthroughUrl = placementContent.callbacks.clickthrough ?: ""
      // Gson uses unsafe instance construction mechanism which may cause non-nullable field to be null
      // https://stackoverflow.com/questions/52837665/why-kotlin-data-classes-can-have-nulls-in-non-nullable-fields-with-gson
      val content = placementContent.content ?: JsonNull.INSTANCE
      val callbackConnector = PlacementCallbackConnectorImpl(
          callbackRequestTracker,
          impressionUrl,
          clickthroughUrl
      )
      PlacementImpl(
          content,
          impressionUrl,
          clickthroughUrl,
          callbackConnector
      )
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
