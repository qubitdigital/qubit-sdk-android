package com.qubit.android.sdk.internal.placement.model

/**
 * Additional criteria used to query for the placement.
 */
data class PlacementPreviewOptions(

    /**
     * Unique ID of the campaign to preview. Passing this will fetch placements data for campaign preview.
     */
    val campaignId: String?,

    /**
     * Unique ID of the experience to preview. Passing this will fetch placements data for experience preview.
     * This must be used in conjunction with [campaignId].
     */
    val experienceId: String?
)
