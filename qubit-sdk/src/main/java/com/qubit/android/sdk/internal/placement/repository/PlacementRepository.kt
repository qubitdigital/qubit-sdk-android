package com.qubit.android.sdk.internal.placement.repository

import com.qubit.android.sdk.internal.placement.model.PlacementModel

internal interface PlacementRepository {

  fun save(key: String, placement: PlacementModel)

  fun load(key: String): PlacementModel?

  fun remove(key: String)
}
