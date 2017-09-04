package com.qubit.android.sdk.internal;

import android.content.Context;
import com.google.gson.Gson;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepository;
import com.qubit.android.sdk.internal.configuration.ConfigurationRepositoryImpl;
import com.qubit.android.sdk.internal.configuration.ConfigurationServiceImpl;
import com.qubit.android.sdk.internal.eventtracker.EventTrackerImpl;
import com.qubit.android.sdk.internal.eventtracker.repository.EventsRepository;
import com.qubit.android.sdk.internal.eventtracker.repository.EventsRepositoryMock;
import com.qubit.android.sdk.internal.network.NetworkStateServiceImpl;

public class SDK {

  private NetworkStateServiceImpl networkStateService;
  private ConfigurationServiceImpl configurationService;
  private EventTrackerImpl eventTracker;

  public SDK(Context appContext, String trackingId) {
    this.networkStateService = new NetworkStateServiceImpl(appContext);

    ConfigurationRepository configurationRepository = new ConfigurationRepositoryImpl(appContext, new Gson());
    this.configurationService =
        new ConfigurationServiceImpl(trackingId, networkStateService, configurationRepository);

    EventsRepository eventsRepository = new EventsRepositoryMock();
    this.eventTracker = new EventTrackerImpl(appContext, configurationService, networkStateService, eventsRepository);
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
