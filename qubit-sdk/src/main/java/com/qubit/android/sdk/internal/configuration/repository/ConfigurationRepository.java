package com.qubit.android.sdk.internal.configuration.repository;

public interface ConfigurationRepository {

  void save(ConfigurationModel configuration);
  ConfigurationModel load();

}
