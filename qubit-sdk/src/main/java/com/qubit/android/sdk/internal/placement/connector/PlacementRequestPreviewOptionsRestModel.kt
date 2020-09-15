package com.qubit.android.sdk.internal.placement.connector

import com.qubit.android.sdk.internal.placement.model.PlacementPreviewOptions

data class PlacementRequestPreviewOptionsRestModel(
    val campaignId: String?,
    val experienceId: String?
) {

  constructor(previewOptions: PlacementPreviewOptions) : this(
      previewOptions.campaignId,
      previewOptions.experienceId
  )
}
