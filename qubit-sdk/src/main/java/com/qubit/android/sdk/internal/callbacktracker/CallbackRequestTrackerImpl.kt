package com.qubit.android.sdk.internal.callbacktracker

import com.qubit.android.sdk.internal.callbacktracker.repository.CallbackRequestRepository
import com.qubit.android.sdk.internal.common.logging.QBLogger
import com.qubit.android.sdk.internal.common.service.QBService
import com.qubit.android.sdk.internal.network.NetworkStateService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CallbackRequestTrackerImpl(
    private val networkStateService: NetworkStateService,
    private val callbackRequestRepository: CallbackRequestRepository
) : CallbackRequestTracker, QBService(SERVICE_NAME) {

  companion object {
    private const val SERVICE_NAME = "CallbackRequestTracker"
    private val LOGGER = QBLogger.getFor(SERVICE_NAME)
    private const val BASE_URL_PLACEHOLDER = "http://localhost/"
  }

  private val callbackRequestApi: CallbackRequestAPI
  private val networkStateListener: NetworkStateService.NetworkStateListener
  private val sendRequestTask = SendRequestTask()

  private var isConnected = false

  init {
    callbackRequestApi = Retrofit.Builder()
        .baseUrl(BASE_URL_PLACEHOLDER)  // https://stackoverflow.com/questions/34842390/how-to-setup-retrofit-with-no-baseurl
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CallbackRequestAPI::class.java)
    networkStateListener = NetworkStateService.NetworkStateListener {
      postTask(NetworkStateChangeTask(it))
    }
  }

  override fun scheduleRequest(url: String) {
    postTask(ScheduleRequestTask(url))
  }

  override fun onStart() {
    callbackRequestRepository.init()
    networkStateService.registerNetworkStateListener(networkStateListener)
  }

  override fun onStop() {
    networkStateService.unregisterNetworkStateListener(networkStateListener)
  }

  private fun sendNextPendingRequest() {
    if (callbackRequestRepository.size() > 0) {
      removeTask(sendRequestTask)
      postTask(sendRequestTask)
    }
  }

  inner class NetworkStateChangeTask constructor(
      private val isConnected: Boolean
  ) : Runnable {

    override fun run() {
      LOGGER.d("Network state changed. Connected: $isConnected")
      this@CallbackRequestTrackerImpl.isConnected = isConnected
      if (isConnected) {
        sendNextPendingRequest()
      }
    }
  }

  inner class ScheduleRequestTask(
      private val url: String
  ) : Runnable {

    override fun run() {
      LOGGER.d("Scheduling request to $url")
      callbackRequestRepository.insert(url)
      sendNextPendingRequest()
    }
  }

  inner class SendRequestTask : Runnable {

    override fun run() {
      LOGGER.d("Send callback request task")

      if (!this@CallbackRequestTrackerImpl.isConnected) {
        LOGGER.d("Device offline - aborting")
        return
      }

      val url = callbackRequestRepository.fetchFirst()
      url?.let {
        LOGGER.d("Sending request to $url")
        postRequest(url)
        sendNextPendingRequest()
      }
    }

    private fun postRequest(url: String) {
      try {
        val response = callbackRequestApi.postCallbackRequest(url).execute()
        if (response.isSuccessful) {
          LOGGER.d("Successfully triggered placement callback to $url")
        } else {
          LOGGER.d("Placement callback to $url failed with error code ${response.code()}")
        }
      } catch (e: Exception) {
        LOGGER.e("Failed to send placement callback to $url", e)
      }
    }
  }
}
