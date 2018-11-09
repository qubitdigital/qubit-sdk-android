package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.BaseTest
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import org.mockito.Mockito.`when` as whenever

class ExperienceConnectorImplTest : BaseTest() {

  @Mock
  private lateinit var mockExperienceAPI: ExperienceAPI

  @Mock
  private lateinit var mockRetrofitCallback: Call<ExperienceModel>

  @Mock
  private lateinit var mockResponse: Response<ExperienceModel>

  @Mock
  private lateinit var experienceModel: ExperienceModel

  private val mockTrackingID = "someTrackingId"
  private val mockContextID = "someContextId"

  private lateinit var experienceConnector: ExperienceConnector

  override fun setup() {
    super.setup()
    experienceConnector = ExperienceConnectorImpl(mockTrackingID, mockContextID, mockExperienceAPI)
  }

  override fun tearDown() {
    super.tearDown()
    verifyNoMoreInteractions(mockExperienceAPI, mockRetrofitCallback, mockResponse, experienceModel)
  }

  @Test
  fun `should return experienceModel after getExperienceModel is called`() {
    mockExperienceApi()
    whenever(mockRetrofitCallback.execute()).thenReturn(mockResponse)
    whenever(mockResponse.body()).thenReturn(experienceModel)

    val experienceModel = experienceConnector.getExperienceModel()

    verifyExperienceApiAndResponseCalls()

    assertThat(experienceModel, instanceOf(ExperienceModel::class.java))
  }

  private fun mockExperienceApi() = whenever(
      mockExperienceAPI.getExperience(mockTrackingID, mockContextID)
  ).thenReturn(mockRetrofitCallback)

  private fun verifyExperienceApiCalls() {
    verify(mockExperienceAPI).getExperience(mockTrackingID, mockContextID)
    verify(mockRetrofitCallback).execute()
  }

  private fun verifyExperienceApiAndResponseCalls() {
    verifyExperienceApiCalls()
    verify(mockResponse).body()
  }

  @Test
  fun `should return null after getExperienceModel is called and IOException is rise`() {
    mockExperienceApi()
    whenever(mockRetrofitCallback.execute()).thenThrow(IOException::class.java)

    val experienceModel = experienceConnector.getExperienceModel()

    verifyExperienceApiCalls()

    assertNull(experienceModel)
  }

  @Test
  fun `should return null after getExperienceModel is called and RuntimeException is rise`() {
    mockExperienceApi()
    whenever(mockRetrofitCallback.execute()).thenThrow(RuntimeException::class.java)

    val experienceModel = experienceConnector.getExperienceModel()

    verifyExperienceApiCalls()

    assertNull(experienceModel)
  }

  @Test
  fun `should return null after getExperienceModel is called and response body is empty`() {
    mockExperienceApi()
    whenever(mockExperienceAPI.getExperience(mockTrackingID, mockContextID)).thenReturn(mockRetrofitCallback)
    whenever(mockRetrofitCallback.execute()).thenReturn(mockResponse)
    whenever(mockResponse.body()).thenReturn(null)

    val experienceModel = experienceConnector.getExperienceModel()

    verifyExperienceApiAndResponseCalls()

    assertNull(experienceModel)
  }
}