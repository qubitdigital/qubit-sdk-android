package com.qubit.android.sdk.internal.experience.interactor

import com.qubit.android.BaseTest
import com.qubit.android.sdk.api.tracker.OnExperienceError
import com.qubit.android.sdk.api.tracker.OnExperienceSuccess
import com.qubit.android.sdk.internal.configuration.Configuration
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnector
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnectorBuilder
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import com.qubit.android.sdk.internal.experience.model.ExperiencePayload
import com.qubit.android.sdk.internal.experience.service.ExperienceService
import org.json.JSONObject
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as whenever

class ExperienceInteractorImplTest : BaseTest() {

  @Mock
  private lateinit var mockExperienceConnectorBuilder: ExperienceConnectorBuilder

  @Mock
  private lateinit var mockExperienceService: ExperienceService

  @Mock
  private lateinit var mockOnSuccessFunction: OnExperienceSuccess

  @Mock
  private lateinit var mockOnErrorFunction: OnExperienceError

  @Mock
  private lateinit var mockExperienceConnector: ExperienceConnector

  @Mock
  private lateinit var mockConfiguration: Configuration

  private lateinit var experienceInteractor: ExperienceInteractor

  override fun setup() {
    super.setup()
    experienceInteractor = ExperienceInteractorImpl(mockExperienceConnectorBuilder, mockExperienceService)
  }

  override fun tearDown() {
    super.tearDown()
    verifyNoMoreInteractions(mockExperienceConnectorBuilder, mockExperienceService, mockOnSuccessFunction, mockOnErrorFunction)
  }

  @Test
  fun `should return ExperienceList in callback from local cache after fetch experience is called `() {
    val experienceIdsList = arrayListOf<Int>()

    whenever(mockExperienceService.experienceData).thenReturn(getMockExperienceModel())

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, experienceIdsList, null, null, null)

    verify(mockExperienceService).experienceData
    verify(mockOnSuccessFunction).invoke(anyList())
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
  fun `should return ExperienceList in callback from remote after fetch experience is called `() {
    val experienceIdsList = arrayListOf(139731, 143401)

    val mockExperienceApiHost = "someUrl"

    whenever(mockExperienceService.experienceData).thenReturn(null)
    whenever(mockExperienceConnectorBuilder.buildFor(anyString())).thenReturn(mockExperienceConnector)
    whenever(mockExperienceService.configuration).thenReturn(mockConfiguration)
    whenever(mockConfiguration.experienceApiHost).thenReturn(mockExperienceApiHost)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, experienceIdsList, null, null, null)

    verify(mockExperienceService).experienceData
    verify(mockExperienceConnectorBuilder).buildFor(mockExperienceApiHost)
    verify(mockExperienceService).configuration
  }
}
