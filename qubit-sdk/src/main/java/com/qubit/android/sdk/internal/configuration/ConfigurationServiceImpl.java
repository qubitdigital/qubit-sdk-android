package com.qubit.android.sdk.internal.configuration;

import android.content.Context;
import com.qubit.android.sdk.internal.network.NetworkStateService;

public class ConfigurationServiceImpl implements ConfigurationService {

  private final Context context;
  private final String trackingId;
  private final NetworkStateService networkStateService;
  private final ConfigurationRepository configurationRepository;

  public ConfigurationServiceImpl(Context context, String trackingId, NetworkStateService networkStateService,
                                  ConfigurationRepository configurationRepository) {
    this.context = context;
    this.trackingId = trackingId;
    this.networkStateService = networkStateService;
    this.configurationRepository = configurationRepository;
  }

  @Override
  public void registerConfigurationListener(ConfigurationListener listener) {

  }

  @Override
  public boolean isInitialized() {
    return false;
  }

  @Override
  public Configuration getConfiguration() {
    return null;
  }

  public void start() {
    //TODO
    networkStateService.registerNetworkStateListener(new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        // TODO
      }
    });
  }
}
