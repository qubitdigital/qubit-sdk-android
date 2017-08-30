package com.qubit.android.sdk.internal;

import android.content.Context;
import com.qubit.android.sdk.api.logging.QBLogLevel;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepository;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepositoryImpl;
import com.qubit.android.sdk.internal.configuration.ConfigurationServiceImpl;
import com.qubit.android.sdk.internal.eventtracker.EventTrackerImpl;
import com.qubit.android.sdk.internal.network.NetworkStateServiceImpl;

public class SDK {

  public static QBLogLevel logLevel = QBLogLevel.WARN;

  private NetworkStateServiceImpl networkStateService;
  private ConfigurationServiceImpl configurationService;
  private EventTrackerImpl eventTracker;

  public SDK(Context appContext, String trackingId) {
    this.networkStateService = new NetworkStateServiceImpl(appContext);
    ConfigurationRepository configurationRepository = new ConfigurationRepositoryImpl();
    this.configurationService = new ConfigurationServiceImpl(appContext, trackingId, networkStateService,
        configurationRepository);
    this.eventTracker = new EventTrackerImpl(appContext, configurationService, networkStateService);
  }

  public void start() {
    configurationService.start();
    eventTracker.start();
  }

  public EventTrackerImpl getEventTracker() {
    return eventTracker;
  }
}
