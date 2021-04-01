package com.qubit.android.sdk.internal.placement.interactor

import com.google.gson.JsonObject
import com.qubit.android.sdk.api.placement.PlacementMode
import com.qubit.android.sdk.api.placement.PlacementPreviewOptions
import com.qubit.android.sdk.api.tracker.OnPlacementError
import com.qubit.android.sdk.api.tracker.OnPlacementSuccess

interface PlacementInteractor {

  fun fetchPlacement(
      placementId: String,
      mode: PlacementMode?,
      customAttributes: JsonObject?,
      previewOptions: PlacementPreviewOptions,
      onSuccess: OnPlacementSuccess,
      onError: OnPlacementError
  )
}
