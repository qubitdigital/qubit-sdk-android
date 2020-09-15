package com.qubit.android.sdk.internal.placement.interactor

import com.qubit.android.sdk.api.tracker.OnPlacementError
import com.qubit.android.sdk.api.tracker.OnPlacementSuccess
import com.qubit.android.sdk.internal.placement.model.PlacementMode
import com.qubit.android.sdk.internal.placement.model.PlacementPreviewOptions

interface PlacementInteractor {

  fun fetchPlacement(
      placementId: String,
      mode: PlacementMode?,
      previewOptions: PlacementPreviewOptions,
      onSuccess: OnPlacementSuccess,
      onError: OnPlacementError
  )
}
