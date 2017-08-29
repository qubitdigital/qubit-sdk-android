package com.qubit.android.sdk.internal.configuration;

public class ConfigurationImpl implements Configuration {
  private String endpoint; // TODO default "gong-eb.qubit.com"
  private String dataLocation; // TODO default "EU" ?
  private int configurationReloadInterval; // TODO default 60
  private int queueTimeout; // TODO default 60
  private String vertical; // TODO default "ec" ?
  private String namespace; // TODO default ""
  private long propertyId; // TODO default 1234 ?
  private boolean disabled; // TODO default false ?
  private String lookupAttributeUrl; // TODO default "https://lookup.qubit.com" ?
  private int lookupGetRequestTimeout; // TODO default 5 ?
  private int lookupCacheExpireTime; // TODO default 60 ?

  @Override
  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public String getDataLocation() {
    return dataLocation;
  }

  public void setDataLocation(String dataLocation) {
    this.dataLocation = dataLocation;
  }

  @Override
  public int getConfigurationReloadInterval() {
    return configurationReloadInterval;
  }

  public void setConfigurationReloadInterval(int configurationReloadInterval) {
    this.configurationReloadInterval = configurationReloadInterval;
  }

  public int getQueueTimeout() {
    return queueTimeout;
  }

  public void setQueueTimeout(int queueTimeout) {
    this.queueTimeout = queueTimeout;
  }

  @Override
  public String getVertical() {
    return vertical;
  }

  public void setVertical(String vertical) {
    this.vertical = vertical;
  }

  @Override
  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @Override
  public long getPropertyId() {
    return propertyId;
  }

  public void setPropertyId(long propertyId) {
    this.propertyId = propertyId;
  }

  @Override
  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Override
  public String getLookupAttributeUrl() {
    return lookupAttributeUrl;
  }

  public void setLookupAttributeUrl(String lookupAttributeUrl) {
    this.lookupAttributeUrl = lookupAttributeUrl;
  }

  @Override
  public int getLookupGetRequestTimeout() {
    return lookupGetRequestTimeout;
  }

  public void setLookupGetRequestTimeout(int lookupGetRequestTimeout) {
    this.lookupGetRequestTimeout = lookupGetRequestTimeout;
  }

  @Override
  public int getLookupCacheExpireTime() {
    return lookupCacheExpireTime;
  }

  public void setLookupCacheExpireTime(int lookupCacheExpireTime) {
    this.lookupCacheExpireTime = lookupCacheExpireTime;
  }
}
