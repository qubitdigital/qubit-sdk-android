package com.qubit.android.sdk.internal;

import android.content.Context;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepository;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepositoryImpl;
import com.qubit.android.sdk.internal.configuration.ConfigurationServiceImpl;
import com.qubit.android.sdk.internal.eventtracker.EventTrackerImpl;
import com.qubit.android.sdk.internal.eventtracker.connector.EventsRestAPIConnectorBuilder;
import com.qubit.android.sdk.internal.eventtracker.connector.EventsRestAPIConnectorBuilderImpl;
import com.qubit.android.sdk.internal.eventtracker.repository.EventsRepository;
import com.qubit.android.sdk.internal.eventtracker.repository.EventsRepositoryMock;
import com.qubit.android.sdk.internal.network.NetworkStateServiceImpl;

public class SDK {

  private NetworkStateServiceImpl networkStateService;
  private ConfigurationServiceImpl configurationService;
  private EventTrackerImpl eventTracker;

  public SDK(Context appContext, String trackingId) {
    this.networkStateService = new NetworkStateServiceImpl(appContext);

    ConfigurationRepository configurationRepository = new ConfigurationRepositoryImpl(appContext);
    this.configurationService =
        new ConfigurationServiceImpl(trackingId, networkStateService, configurationRepository);

    EventsRepository eventsRepository = new EventsRepositoryMock();
    String deviceId = "deviceId"; //new SecureAndroidIdDeviceIdProvider(appContext).getDeviceId();
    EventsRestAPIConnectorBuilder eventsRestAPIConnectorBuilder = new EventsRestAPIConnectorBuilderImpl(trackingId);
    this.eventTracker = new EventTrackerImpl(trackingId, deviceId,
        configurationService, networkStateService, eventsRepository, eventsRestAPIConnectorBuilder);
  }

  public void start() {
    networkStateService.start();
    configurationService.start();
    eventTracker.start();
  }

  public EventTrackerImpl getEventTracker() {
    return eventTracker;
  }

}
