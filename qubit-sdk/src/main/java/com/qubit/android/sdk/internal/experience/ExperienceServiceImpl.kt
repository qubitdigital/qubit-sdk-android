package com.qubit.android.sdk.internal.experience

import com.qubit.android.sdk.internal.common.logging.QBLogger
import com.qubit.android.sdk.internal.common.service.QBService
import com.qubit.android.sdk.internal.configuration.ConfigurationService
import com.qubit.android.sdk.internal.experience.ExperienceService.ExperienceListener
import com.qubit.android.sdk.internal.network.NetworkStateService
import java.util.concurrent.CopyOnWriteArraySet

internal class ExperienceServiceImpl(
    private val configurationService: ConfigurationService,
    private val networkStateService: NetworkStateService
) : QBService(SERVICE_NAME), ExperienceService {

  companion object {
    private const val SERVICE_NAME = "ExperienceService"

    @JvmStatic
    private val LOGGER = QBLogger.getFor(SERVICE_NAME)
  }

  private val listeners = CopyOnWriteArraySet<ExperienceListener>()

  override fun onStart() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onStop() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun registerExperienceListener(listener: ExperienceListener) {
    postTask {
      listeners.add(listener)
//      if (currentLookupCache != null && currentLookupCache.getLookupModel() != null) {
//        notifyLookupDataChange(listener, currentLookupCache.getLookupModel())
//      }
    }
  }

  override fun unregisterLookupListener(listener: ExperienceListener) {
    postTask { listeners.remove(listener) }
  }
}