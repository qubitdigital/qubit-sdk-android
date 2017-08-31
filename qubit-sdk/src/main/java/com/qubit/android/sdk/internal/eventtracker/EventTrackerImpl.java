package com.qubit.android.sdk.internal.eventtracker;

import android.content.Context;
import com.qubit.android.sdk.api.tracker.EventTracker;
import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.internal.configuration.Configuration;
import com.qubit.android.sdk.internal.configuration.ConfigurationService;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.network.NetworkStateService;

public class EventTrackerImpl implements EventTracker {

  private static final QBLogger LOGGER = QBLogger.getFor("EventTracker");

  private final Context context;
  private final ConfigurationService configurationService;
  private final NetworkStateService networkStateService;

  public EventTrackerImpl(Context context, ConfigurationService configurationService,
                          NetworkStateService networkStateService) {
    this.context = context;
    this.configurationService = configurationService;
    this.networkStateService = networkStateService;
  }

  @Override
  public void sendEvent(String type, QBEvent event) {
    // TODO
  }

  @Override
  public void enable(boolean enable) {
    // TODO
  }

  public void start() {
    // TODO
    configurationService.registerConfigurationListener(new ConfigurationService.ConfigurationListener() {
      @Override
      public void onInitialization(Configuration configuration) {
        // TODO
      }

      @Override
      public void onConfigurationChange(Configuration configuration) {
        // TODO
      }
    });

    networkStateService.registerNetworkStateListener(new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        // TODO
      }
    });
  }


}
