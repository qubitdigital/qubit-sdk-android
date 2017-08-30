package com.qubit.android.sdk.internal.configuration;

public interface ConfigurationService {

  void registerConfigurationListener(ConfigurationListener listener);

  interface ConfigurationListener {
    void onInitialization(Configuration configuration);
    void onConfigurationChange(Configuration configuration);
  }

}
