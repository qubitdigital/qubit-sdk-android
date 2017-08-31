package com.qubit.android.sdk.internal.configuration;

public interface ConfigurationRepository {

  void save(ConfigurationModel configuration);
  ConfigurationModel load();

}
