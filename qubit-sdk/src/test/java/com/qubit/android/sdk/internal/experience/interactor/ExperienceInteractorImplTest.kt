package com.qubit.android.sdk.internal.experience.interactor

import com.google.gson.JsonObject
import com.qubit.android.BaseTest
import com.qubit.android.sdk.api.tracker.OnExperienceError
import com.qubit.android.sdk.api.tracker.OnExperienceSuccess
import com.qubit.android.sdk.internal.configuration.Configuration
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnector
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnectorBuilder
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import com.qubit.android.sdk.internal.experience.model.ExperiencePayload
import com.qubit.android.sdk.internal.experience.service.ExperienceService
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as whenever

class ExperienceInteractorImplTest : BaseTest() {

  companion object {
    private const val CALLBACK_URL = "https://www.somewebside.com"
    private const val EXPERIENCE_API_HOST = "someUrl"

    private val EXPERIENCE_MODEL = ExperienceModel(arrayListOf(
        ExperiencePayload(JsonObject(), false, 139731, CALLBACK_URL, 75834),
        ExperiencePayload(JsonObject(), false, 143401, CALLBACK_URL, 855620),
        ExperiencePayload(JsonObject(), true, 143640, CALLBACK_URL, 852185)
    ))
  }

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
    experienceInteractor = ExperienceInteractorImpl(mockExperienceConnectorBuilder, mockExperienceService, "someDeviceID")
  }

  override fun tearDown() {
    super.tearDown()
    verifyNoMoreInteractions(mockExperienceConnectorBuilder, mockExperienceService, mockOnSuccessFunction, mockOnErrorFunction)
  }

  @Test
  fun `all parameters not set, cache non-empty, should read from cache`() {
    prepareMocks(EXPERIENCE_MODEL)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(), null, null, null)

    verifyCacheChecked()
    verify(mockOnSuccessFunction).invoke(anyList())
  }

  @Test
  fun `all parameters not set, cache empty, should fetch experiences`() {
    prepareMocks(null)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(139731, 143401), null, null, null)

    verifyCacheChecked()
    verifyRequestSent()
  }

  @Test
  fun `variation set, should fetch experiences`() {
    prepareMocks(EXPERIENCE_MODEL)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(), 1, null, null)

    verifyRequestSent()
  }

  @Test
  fun `preview set for true, should fetch experiences`() {
    prepareMocks(EXPERIENCE_MODEL)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(), null, true, null)

    verifyRequestSent()
  }

  @Test
  fun `preview set for false, other parameters not set, should read from cache`() {
    prepareMocks(EXPERIENCE_MODEL)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(), null, false, null)

    verifyCacheChecked()
    verify(mockOnSuccessFunction).invoke(anyList())
  }

  @Test
  fun `preview set for false, variation set, should fetch experiences`() {
    prepareMocks(EXPERIENCE_MODEL)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(), 1, false, null)

    verifyRequestSent()
  }

  @Test
  fun `preview set for false, ignoreSegments set, should fetch experiences`() {
    prepareMocks(EXPERIENCE_MODEL)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(), null, false, true)

    verifyRequestSent()
  }

  @Test
  fun `ignoreSegments set for true, should fetch experiences`() {
    prepareMocks(EXPERIENCE_MODEL)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(), null, null, true)

    verifyRequestSent()
  }

  @Test
  fun `ignoreSegments set for false, should fetch experiences`() {
    prepareMocks(EXPERIENCE_MODEL)

    experienceInteractor.fetchExperience(mockOnSuccessFunction, mockOnErrorFunction, arrayListOf(), null, null, false)

    verifyRequestSent()
  }

  /******** helper methods ********/

  private fun prepareMocks(cachedModel: ExperienceModel?) {
    whenever(mockExperienceService.experienceData).thenReturn(cachedModel)
    whenever(mockExperienceConnectorBuilder.buildFor(anyString())).thenReturn(mockExperienceConnector)
    whenever(mockExperienceService.configuration).thenReturn(mockConfiguration)
    whenever(mockConfiguration.experienceApiHost).thenReturn(EXPERIENCE_API_HOST)
  }

  private fun verifyCacheChecked() {
    verify(mockExperienceService).experienceData
  }

  private fun verifyRequestSent() {
    verify(mockExperienceConnectorBuilder).buildFor(EXPERIENCE_API_HOST)
    verify(mockExperienceService).configuration
  }
}
