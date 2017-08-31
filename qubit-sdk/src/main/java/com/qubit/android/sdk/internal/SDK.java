package com.qubit.android.sdk.internal;

import android.content.Context;
import com.google.gson.Gson;
import com.qubit.android.sdk.api.initialization.QBLogLevel;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepository;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepositoryImpl;
import com.qubit.android.sdk.internal.configuration.ConfigurationServiceImpl;
import com.qubit.android.sdk.internal.eventtracker.EventTrackerImpl;
import com.qubit.android.sdk.internal.network.NetworkStateServiceImpl;

public class SDK {

  public static QBLogLevel logLevel = QBLogLevel.WARN;

  private final Context appContext;
  private final String trackingId;
  private NetworkStateServiceImpl networkStateService;
  private ConfigurationServiceImpl configurationService;
  private EventTrackerImpl eventQueue;

  public SDK(Context appContext, String trackingId) {
    this.appContext = appContext;
    this.trackingId = trackingId;
    this.networkStateService = new NetworkStateServiceImpl(appContext);
    ConfigurationRepository configurationRepository = new ConfigurationRepositoryImpl(appContext, new Gson());
    this.configurationService = new ConfigurationServiceImpl(appContext, trackingId, networkStateService,
        configurationRepository);
    this.eventQueue = new EventTrackerImpl(appContext, configurationService, networkStateService);
  }

  public void start() {
    networkStateService.start(appContext);
    configurationService.start();
    eventQueue.start();
  }
}
