package com.qubit.android.sdk.internal.lookup;

import com.qubit.android.sdk.internal.common.service.QBService;
import com.qubit.android.sdk.internal.configuration.Configuration;
import com.qubit.android.sdk.internal.configuration.ConfigurationService;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.lookup.connector.LookupConnector;
import com.qubit.android.sdk.internal.lookup.connector.LookupConnectorBuilder;
import com.qubit.android.sdk.internal.lookup.model.LookupModel;
import com.qubit.android.sdk.internal.network.NetworkStateService;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;


public class LookupServiceImpl extends QBService implements LookupService {

  private static final String SERVICE_NAME = "LookupService";
  private static final QBLogger LOGGER = QBLogger.getFor(SERVICE_NAME);

  private final String trackingId;
  private final String deviceId;
  private final ConfigurationService configurationService;
  private final NetworkStateService networkStateService;
  private final LookupConnectorBuilder lookupConnectorBuilder;

  private final ConfigurationService.ConfigurationListener configurationListener;
  private final NetworkStateService.NetworkStateListener networkStateListener;

  private Collection<LookupListener> listeners = new CopyOnWriteArraySet<>();
  private LookupModel currentLookupModel = null;

  private Configuration currentConfiguration = null;
  private boolean isConnected = false;
  private LookupConnector lookupConnector = null;


  public LookupServiceImpl(String trackingId, String deviceId,
                           ConfigurationService configurationService, NetworkStateService networkStateService,
                           LookupConnectorBuilder lookupConnectorBuilder) {
    super(SERVICE_NAME);
    this.trackingId = trackingId;
    this.deviceId = deviceId;
    this.configurationService = configurationService;
    this.networkStateService = networkStateService;
    this.lookupConnectorBuilder = lookupConnectorBuilder;
    configurationListener = new ConfigurationService.ConfigurationListener() {
      @Override
      public void onConfigurationChange(Configuration configuration) {
        postTask(new ConfigurationChangeTask(configuration));
      }
    };
    networkStateListener = new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        postTask(new NetworkStateChangeTask(isConnected));
      }
    };

  }

  @Override
  protected void onStart() {
    configurationService.registerConfigurationListener(configurationListener);
    networkStateService.registerNetworkStateListener(networkStateListener);
  }

  @Override
  protected void onStop() {
    configurationService.unregisterConfigurationListener(configurationListener);
    networkStateService.unregisterNetworkStateListener(networkStateListener);
  }

  @Override
  public void registerLookupListener(final LookupListener listener) {
    postTask(new Runnable() {
      @Override
      public void run() {
        listeners.add(listener);
        if (currentLookupModel != null) {
          notifyLookupDataChange(listener, currentLookupModel);
        }
      }
    });

  }

  @Override
  public void unregisterLookupListener(final LookupListener listener) {
    postTask(new Runnable() {
      @Override
      public void run() {
        listeners.remove(listener);
      }
    });
  }

  private class ConfigurationChangeTask implements Runnable {
    private final Configuration configuration;

    ConfigurationChangeTask(Configuration configuration) {
      this.configuration = configuration;
    }

    @Override
    public void run() {
      LOGGER.d("Configuration Changed");
      currentConfiguration = configuration;
      try {
        lookupConnector = lookupConnectorBuilder.buildFor(currentConfiguration.getEndpoint());
      } catch (IllegalArgumentException e) {
        LOGGER.e("Cannot create Rest API connector. Most likely endpoint url is incorrect.", e);
      }
    }
  }

  private class NetworkStateChangeTask implements Runnable {
    private final boolean isConnected;

    NetworkStateChangeTask(boolean connected) {
      this.isConnected = connected;
    }

    @Override
    public void run() {
      LOGGER.d("Network state changed. Connected: " + isConnected);
      LookupServiceImpl.this.isConnected = isConnected;
      scheduleNextLookupRequestTask();
    }
  }

  private void notifyListenersLookupDataChange() {
    for (LookupListener listener : listeners) {
      notifyLookupDataChange(listener, currentLookupModel);
    }
  }

  private static void notifyLookupDataChange(LookupListener listener, LookupData lookupData) {
    if (listener != null) {
      listener.onLookupDataChange(lookupData);
    }
  }

  private void scheduleNextLookupRequestTask() {
    // TODO
  }

}
