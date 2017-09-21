package com.qubit.android.sdk.internal.configuration;

public interface Configuration {

  String getEndpoint();

  int getConfigurationReloadInterval();

  int getQueueTimeout();

  String getVertical();

  String getNamespace();

  long getPropertyId();

  boolean isDisabled();

  String getLookupAttributeUrl();

  int getLookupGetRequestTimeout();

  int getLookupCacheExpireTime();


}
