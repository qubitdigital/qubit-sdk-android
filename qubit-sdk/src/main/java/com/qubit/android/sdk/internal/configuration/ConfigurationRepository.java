package com.qubit.android.sdk.internal.configuration;

public interface ConfigurationRepository {

  void save(Configuration configuration);
  Configuration load();

}
