package com.qubit.android.sdk.internal.configuration;

public interface ConfigurationService {

  void registerConfigurationListener(ConfigurationListener listener);
  void unregisterConfigurationListener(ConfigurationListener configurationListener);

  interface ConfigurationListener {
    void onConfigurationChange(Configuration configuration);
  }

}
