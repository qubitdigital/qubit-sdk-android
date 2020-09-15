package com.qubit.android.sdk.internal.placement.connector

import com.qubit.android.sdk.internal.placement.model.PlacementMode
import com.qubit.android.sdk.internal.placement.model.PlacementModel
import com.qubit.android.sdk.internal.placement.model.PlacementPreviewOptions

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
