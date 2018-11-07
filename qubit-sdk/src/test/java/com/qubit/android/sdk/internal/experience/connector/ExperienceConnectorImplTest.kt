package com.qubit.android.sdk.internal.experience.connector

import com.qubit.android.BaseTest
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import retrofit2.Call
import retrofit2.Callback
import org.mockito.Mockito.`when` as whenever

class ExperienceConnectorImplTest : BaseTest() {

  @Mock
  private lateinit var mockExperienceAPI: ExperienceAPI

  @Mock
  private lateinit var mockRetrofitCallback: Call<ExperienceModel>

  @Captor
  private lateinit var captorOnSuccessListener: ArgumentCaptor<Callback<ExperienceModel>>

  private val mockTrackingID = "someTrackingId"
  private val mockContextID = "someContextId"

  private lateinit var experienceConnector: ExperienceConnector

  override fun setup() {
    super.setup()
    experienceConnector = ExperienceConnectorImpl(mockTrackingID, mockContextID, mockExperienceAPI)
  }

  @Test
  fun `should return experienceModel after getExperienceModel is called`() {
    whenever(mockExperienceAPI.getExperience(anyString(), anyString())).thenReturn(mockRetrofitCallback)
    whenever(mockRetrofitCallback.enqueue(captorOnSuccessListener.capture()))

//    verify(mockRetrofitCallback).enqueue(captorOnSuccessListener.capture())

//    val experienceModel = experienceConnector.getExperienceModel()
  }

  @Test
  fun `should throw IOException after getExperienceModel is called`() {

  }

  @Test
  fun `should throw RuntimeException after getExperienceModel is called`() {

  }

  @Test
  fun getExperienceModel() {
  }
}