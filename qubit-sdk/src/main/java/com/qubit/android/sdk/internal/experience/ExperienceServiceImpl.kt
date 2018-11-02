package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.common.logging.QBLogger
import com.qubit.android.sdk.internal.common.service.QBService
import com.qubit.android.sdk.internal.common.util.DateTimeUtils
import com.qubit.android.sdk.internal.configuration.Configuration
import com.qubit.android.sdk.internal.configuration.ConfigurationService
import com.qubit.android.sdk.internal.configuration.ConfigurationService.ConfigurationListener
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnector
import com.qubit.android.sdk.internal.experience.connector.ExperienceConnectorBuilder
import com.qubit.android.sdk.internal.experience.model.ExperienceModel
import com.qubit.android.sdk.internal.experience.repository.ExperienceCache
import com.qubit.android.sdk.internal.experience.repository.ExperienceRepository
import com.qubit.android.sdk.internal.network.NetworkStateService
import com.qubit.android.sdk.internal.network.NetworkStateService.NetworkStateListener

class ExperienceServiceImpl(
    private val configurationService: ConfigurationService,
    private val networkStateService: NetworkStateService,
    private val experienceRepository: ExperienceRepository,
    private val experienceConnectorBuilder: ExperienceConnectorBuilder
) : QBService(SERVICE_NAME), ExperienceService {

  companion object {
    private const val SERVICE_NAME = "ExperienceService"

    private const val EXP_BACKOFF_BASE_TIME_SECS = 1
    private const val EXP_BACKOFF_MAX_SENDING_ATTEMPTS = 7
    private const val MAX_RETRY_INTERVAL_SECS = 60 * 5

    @JvmStatic
    private val LOGGER = QBLogger.getFor(SERVICE_NAME)
  }

  private val configurationListener: ConfigurationService.ConfigurationListener
  private val networkStateListener: NetworkStateService.NetworkStateListener

  private var initTime: Long = 0
  private var currentExperienceCache: ExperienceCache? = null

  private var experienceConnector: ExperienceConnector? = null
  private var currentConfiguration: Configuration? = null
  private var isConnected = false

  private val experienceRequestTask = ExperienceRequestTask()

  private var requestAttempts = 0
  private var lastAttemptTime: Long = 0

  override val experienceData: ExperienceModel?
    get() = currentExperienceCache?.experienceModel

  override val configuration: Configuration?
    get() = currentConfiguration

  init {
    configurationListener = ConfigurationListener {
      configuration -> postTask(ConfigurationChangeTask(configuration))
    }
    networkStateListener = NetworkStateListener {
      isConnected -> postTask(NetworkStateChangeTask(isConnected))
    }
  }

  override fun onStart() {
//    postTask(InitialTask())
    configurationService.registerConfigurationListener(configurationListener)
    networkStateService.registerNetworkStateListener(networkStateListener)
  }

  override fun onStop() {
    configurationService.unregisterConfigurationListener(configurationListener)
    networkStateService.unregisterNetworkStateListener(networkStateListener)
  }

  private inner class InitialTask : Runnable {
    override fun run() {
      initTime = System.currentTimeMillis()
//      scheduleFailoverLookupTask()
      scheduleNextExperienceRequestTask()
    }
  }

  private inner class ConfigurationChangeTask(private val configuration: Configuration) : Runnable {

    override fun run() {
      LOGGER.d("Configuration Changed")
      currentConfiguration = configuration
      try {
        experienceConnector = experienceConnectorBuilder.buildFor(configuration.experienceApiHost)
        clearAttempts()
        scheduleNextExperienceRequestTask()
      } catch (e: IllegalArgumentException) {
        LOGGER.e("Cannot create Rest API connector. Most likely endpoint url is incorrect.", e)
      }

    }
  }

  private inner class NetworkStateChangeTask internal constructor(private val isConnected: Boolean) : Runnable {

    override fun run() {
      LOGGER.d("Network state changed. Connected: $isConnected")
      this@ExperienceServiceImpl.isConnected = isConnected
      if (isConnected) {
        clearAttempts()
        invalidateLookupCache()
      }
      scheduleNextExperienceRequestTask()
    }
  }

  private inner class ExperienceRequestTask : Runnable {

    override fun run() {
      LOGGER.d("Requesting experience")
      if (isExperienceUpToDate()) {
        scheduleNextExperienceRequestTask()
        return
      }
      if (experienceConnector == null) {
        LOGGER.d("Experience connector is not defined yet.")
      }
      if (!isConnected) {
        return
      }

      val newExperienceModel = experienceConnector?.getExperienceModel()
      if (newExperienceModel != null) {
        registerSuccessfulAttempt()
        currentExperienceCache = ExperienceCache(newExperienceModel, System.currentTimeMillis())
        currentExperienceCache?.let { experienceRepository.save(it) }
        LOGGER.d("New lookup downloaded: $newExperienceModel")
      } else {
        registerFailedAttempt()
        LOGGER.d("New lookup request failed. Current lookup: $currentExperienceCache")
      }

      scheduleNextExperienceRequestTask()
    }
  }

  private fun invalidateLookupCache() {
    currentExperienceCache?.let {
      it.lastUpdateTimestamp = 0
    }
  }

  private fun registerSuccessfulAttempt() {
    clearAttempts()
  }

  private fun registerFailedAttempt() {
    requestAttempts++
    lastAttemptTime = System.currentTimeMillis()
  }

  private fun scheduleNextExperienceRequestTask() {
    removeTask(experienceRequestTask)
    if (!isConnected || currentConfiguration == null || experienceConnector == null) {
      return
    }

    val timeMsToNextRequest = if (requestAttempts > 0)
      evaluateTimeMsToNextRetry()
    else
      evaluateTimeMsToExpiration()

    if (timeMsToNextRequest > 0) {
      postTaskDelayed(experienceRequestTask, timeMsToNextRequest)
      LOGGER.d("Next LookupRequestTask scheduled for $timeMsToNextRequest")
    } else {
      postTask(experienceRequestTask)
      LOGGER.d("Next LookupRequestTask scheduled for NOW")
    }
  }

  private fun evaluateTimeMsToNextRetry(): Long {
    val nextRetryIntervalMs = DateTimeUtils.secToMs(evaluateIntervalSecsToNextRetry(requestAttempts))
    val nextRetryTimeMs = lastAttemptTime + nextRetryIntervalMs
    val now = System.currentTimeMillis()
    return Math.max(nextRetryTimeMs - now, 0)
  }

  private fun evaluateIntervalSecsToNextRetry(sendingAttemptsDone: Int): Long {
    return if (sendingAttemptsDone > EXP_BACKOFF_MAX_SENDING_ATTEMPTS) {
      MAX_RETRY_INTERVAL_SECS.toLong()
    } else {
      (1L shl sendingAttemptsDone - 1) * EXP_BACKOFF_BASE_TIME_SECS
    }
  }

  private fun evaluateTimeMsToExpiration(): Long {
    if (currentExperienceCache == null) {
      return 0
    }
    val experienceExpiryTimeMs = DateTimeUtils.secToMs(currentConfiguration?.lookupCacheExpireTime?.toLong() ?: 0)
    val nextDownloadTime = (currentExperienceCache?.lastUpdateTimestamp ?: 0) + experienceExpiryTimeMs
    val now = System.currentTimeMillis()
    return if (nextDownloadTime > now) nextDownloadTime - now else 0
  }

  private fun clearAttempts() {
    requestAttempts = 0
    lastAttemptTime = 0
  }

  private fun isExperienceUpToDate(): Boolean {
    return currentExperienceCache?.let {
      val experienceExpiryTimeMs = DateTimeUtils.secToMs(currentConfiguration?.lookupCacheExpireTime?.toLong() ?: 0)
      val nextDownloadTime = (currentExperienceCache?.lastUpdateTimestamp ?: 0) + experienceExpiryTimeMs
      val now = System.currentTimeMillis()
      nextDownloadTime > now
    } ?: false
  }
}