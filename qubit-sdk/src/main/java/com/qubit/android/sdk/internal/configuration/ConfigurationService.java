package com.qubit.android.sdk.internal.configuration;

public interface ConfigurationService {

  void registerInitializationListener(InitializationListener listener);
  boolean isInitialized();
  Configuration getConfiguration();


  interface InitializationListener {
    void onInitializationEnd(Configuration configuration);
  }
}
