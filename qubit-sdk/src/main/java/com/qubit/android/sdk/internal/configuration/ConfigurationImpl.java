package com.qubit.android.sdk.internal.configuration;

public class ConfigurationImpl implements Configuration {

  private static final String DEFAULT_ENDPOINT = "gong-eb.qubit.com";
  private static final String DEFAULT_DATA_LOCATION = "EU";
  private static final int DEFAULT_CONFIGURATION_RELOAD_INTERVAL = 60;
  private static final int DEFAULT_QUEUE_TIMEOUT = 60;
  private static final String DEFAULT_VERTICAL = "ec";
  private static final String DEFAULT_NAMESPACE = "";
  private static final int DEFAULT_PROPERTY_ID = 1234;
  private static final boolean DEFAULT_DISABLED = false;
  private static final String DEFAULT_LOOKUP_ATTRIBUTE_URL = "https://lookup.qubit.com";
  private static final int DEFAULT_LOOKUP_GET_REQUEST_TIMEOUT = 5;
  private static final int DEFAULT_LOOKUP_CACHE_EXPIRE_TIME = 60;

  private String endpoint;
  private String dataLocation;
  private int configurationReloadInterval;
  private int queueTimeout;
  private String vertical;
  private String namespace;
  private long propertyId;
  private boolean disabled;
  private String lookupAttributeUrl;
  private int lookupGetRequestTimeout;
  private int lookupCacheExpireTime;
  private Long lastUpdateTimestamp;

  public ConfigurationImpl() {
    endpoint = DEFAULT_ENDPOINT;
    dataLocation = DEFAULT_DATA_LOCATION;
    configurationReloadInterval = DEFAULT_CONFIGURATION_RELOAD_INTERVAL;
    queueTimeout = DEFAULT_QUEUE_TIMEOUT;
    vertical = DEFAULT_VERTICAL;
    namespace = DEFAULT_NAMESPACE;
    propertyId = DEFAULT_PROPERTY_ID;
    disabled = DEFAULT_DISABLED;
    lookupAttributeUrl = DEFAULT_LOOKUP_ATTRIBUTE_URL;
    lookupGetRequestTimeout = DEFAULT_LOOKUP_GET_REQUEST_TIMEOUT;
    lookupCacheExpireTime = DEFAULT_LOOKUP_CACHE_EXPIRE_TIME;
    lastUpdateTimestamp = null;
  }

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

  @Override
  public Long getLastUpdateTimestamp() {
    return lastUpdateTimestamp;
  }

  public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
    this.lastUpdateTimestamp = lastUpdateTimestamp;
  }

  @Override
  public String toString() {
    return "ConfigurationImpl{" +
        "endpoint='" + endpoint + '\'' +
        ", dataLocation='" + dataLocation + '\'' +
        ", configurationReloadInterval=" + configurationReloadInterval +
        ", queueTimeout=" + queueTimeout +
        ", vertical='" + vertical + '\'' +
        ", namespace='" + namespace + '\'' +
        ", propertyId=" + propertyId +
        ", disabled=" + disabled +
        ", lookupAttributeUrl='" + lookupAttributeUrl + '\'' +
        ", lookupGetRequestTimeout=" + lookupGetRequestTimeout +
        ", lookupCacheExpireTime=" + lookupCacheExpireTime +
        ", lastUpdateTimestamp=" + lastUpdateTimestamp +
        '}';
  }
}
