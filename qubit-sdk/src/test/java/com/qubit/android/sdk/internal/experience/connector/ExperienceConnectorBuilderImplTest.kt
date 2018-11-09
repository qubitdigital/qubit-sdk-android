package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.BaseTest
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert.assertThat
import org.junit.Test

class ExperienceConnectorBuilderImplTest : BaseTest() {

  private val mockTrackingID = "someTrackingId"
  private val mockContextID = "someContextId"

  private lateinit var experienceConnectorBuilder: ExperienceConnectorBuilder

  override fun setup() {
    super.setup()
    experienceConnectorBuilder = ExperienceConnectorBuilderImpl(mockTrackingID, mockContextID)
  }

  @Test
  fun `should return ExperienceConnector instance after buildFor is called`() {
    val mockURL = "www.someurl.com"

    val experienceApi = experienceConnectorBuilder.buildFor(mockURL)

    assertThat(experienceApi, instanceOf(ExperienceConnector::class.java))
  }
}