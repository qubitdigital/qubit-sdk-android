package com.qubit.android.sdk.internal.experience.interactor

import com.qubit.android.BaseTest
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnectorBuilder
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import com.qubit.android.sdk.internal.experience.model.ExperiencePayload
import com.qubit.android.sdk.internal.experience.service.ExperienceService
import org.json.JSONObject
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as whenever

class ExperienceInteractorImplTest : BaseTest() {

  @Mock
  private lateinit var experienceConnectorBuilder: ExperienceConnectorBuilder

  @Mock
  private lateinit var experienceService: ExperienceService

  private lateinit var experienceInteractor: ExperienceInteractor

  override fun setup() {
    super.setup()
    experienceInteractor = ExperienceInteractorImpl(experienceConnectorBuilder, experienceService)
  }

  override fun tearDown() {
    super.tearDown()
    verifyNoMoreInteractions(experienceConnectorBuilder, experienceService)
  }

  @Test
  fun `fetch experience from local cache`() { //TODO test callbacks
    val experienceIdsList = arrayListOf<Int>()

    whenever(experienceService.experienceData).thenReturn(getMockExperienceModel())

    experienceInteractor.fetchExperience({}, {}, experienceIdsList, null, null, null)

    verify(experienceService).experienceData
  }

  private fun getMockExperienceModel(): ExperienceModel {
    val mockCallbackUrl = "https://www.somewebside.com"

    val experienceMockList = arrayListOf(
        ExperiencePayload(JSONObject(), false, 139731, mockCallbackUrl, 75834),
        ExperiencePayload(JSONObject(), false, 143401, mockCallbackUrl, 855620),
        ExperiencePayload(JSONObject(), true, 143640, mockCallbackUrl, 852185)
    )

    return ExperienceModel(experienceMockList)
  }

  @Test
  fun `fetch experience and filter it`() {

  }

  @Test
  fun `fetch experience when one of flags is active`() {
    val experienceIdsList = arrayListOf<Int>()

    experienceInteractor.fetchExperience({}, {}, experienceIdsList, null, null, null)
  }
}