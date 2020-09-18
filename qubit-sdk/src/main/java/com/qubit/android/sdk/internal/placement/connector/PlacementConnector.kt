package com.qubit.android.sdk.internal.placement.connector

import com.qubit.android.sdk.api.placement.PlacementMode
import com.qubit.android.sdk.api.placement.PlacementPreviewOptions
import com.qubit.android.sdk.internal.placement.model.PlacementModel

internal typealias OnResponseSuccess = (placementModel: PlacementModel) -> Unit
internal typealias OnResponseFailure = (throwable: Throwable) -> Unit

interface PlacementConnector {

  fun getPlacementModel(
      endpointUrl: String,
      placementId: String,
      mode: PlacementMode,
      deviceId: String,
      previewOptions: PlacementPreviewOptions,
      onResponseSuccess: OnResponseSuccess,
      onResponseFailure: OnResponseFailure
  )
}
