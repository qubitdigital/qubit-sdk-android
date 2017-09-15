package com.qubit.android.sdk.internal.configuration;

import com.qubit.android.sdk.internal.logging.QBLogger;

public class ConfigurationModel implements Configuration {

  private static final QBLogger LOGGER = QBLogger.getFor("ConfigurationModel");

  private static final ConfigurationModel DEFAULT = new ConfigurationModel();

  private static final String ENDPOINT_EU = "gong-eb.qubit.com";
  private static final String ENDPOINT_US = "gong-gc.qubit.com";
  private static final String DATA_LOCATION_EU = "EU";
  private static final String DATA_LOCATION_US = "US";
  private static final String DEFAULT_DATA_LOCATION = DATA_LOCATION_EU;
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

  public ConfigurationModel() {
    endpoint = ENDPOINT_EU;
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

  public Long getLastUpdateTimestamp() {
    return lastUpdateTimestamp;
  }

  public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
    this.lastUpdateTimestamp = lastUpdateTimestamp;
  }

  @Override
  public String toString() {
    return "ConfigurationModel{"
        + "endpoint='" + endpoint + '\''
        + ", dataLocation='" + dataLocation + '\''
        + ", configurationReloadInterval=" + configurationReloadInterval
        + ", queueTimeout=" + queueTimeout
        + ", vertical='" + vertical + '\''
        + ", namespace='" + namespace + '\''
        + ", propertyId=" + propertyId
        + ", disabled=" + disabled
        + ", lookupAttributeUrl='" + lookupAttributeUrl + '\''
        + ", lookupGetRequestTimeout=" + lookupGetRequestTimeout
        + ", lookupCacheExpireTime=" + lookupCacheExpireTime
        + ", lastUpdateTimestamp=" + lastUpdateTimestamp
        + '}';
  }

  public boolean equalsIgnoreLastUpdateTimestamp(ConfigurationModel that) {
    if (configurationReloadInterval != that.configurationReloadInterval) {
      return false;
    }
    if (queueTimeout != that.queueTimeout) {
      return false;
    }
    if (propertyId != that.propertyId) {
      return false;
    }
    if (disabled != that.disabled) {
      return false;
    }
    if (lookupGetRequestTimeout != that.lookupGetRequestTimeout) {
      return false;
    }
    if (lookupCacheExpireTime != that.lookupCacheExpireTime) {
      return false;
    }
    if (endpoint != null ? !endpoint.equals(that.endpoint) : that.endpoint != null) {
      return false;
    }
    if (dataLocation != null ? !dataLocation.equals(that.dataLocation) : that.dataLocation != null) {
      return false;
    }
    if (vertical != null ? !vertical.equals(that.vertical) : that.vertical != null) {
      return false;
    }
    if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) {
      return false;
    }
    if (lookupAttributeUrl != null
        ? !lookupAttributeUrl.equals(that.lookupAttributeUrl) : that.lookupAttributeUrl != null) {
      return false;
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ConfigurationModel that = (ConfigurationModel) o;
    if (!equalsIgnoreLastUpdateTimestamp(that)) {
      return false;
    }

    return lastUpdateTimestamp != null
        ? lastUpdateTimestamp.equals(that.lastUpdateTimestamp) : that.lastUpdateTimestamp == null;

  }

  @SuppressWarnings("checkstyle:magicnumber")
  @Override
  public int hashCode() {
    int result = endpoint != null ? endpoint.hashCode() : 0;
    result = 31 * result + (dataLocation != null ? dataLocation.hashCode() : 0);
    result = 31 * result + configurationReloadInterval;
    result = 31 * result + queueTimeout;
    result = 31 * result + (vertical != null ? vertical.hashCode() : 0);
    result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
    result = 31 * result + (int) (propertyId ^ (propertyId >>> 32));
    result = 31 * result + (disabled ? 1 : 0);
    result = 31 * result + (lookupAttributeUrl != null ? lookupAttributeUrl.hashCode() : 0);
    result = 31 * result + lookupGetRequestTimeout;
    result = 31 * result + lookupCacheExpireTime;
    result = 31 * result + (lastUpdateTimestamp != null ? lastUpdateTimestamp.hashCode() : 0);
    return result;
  }

  public static ConfigurationModel getDefault() {
    return DEFAULT;
  }

  public static String getEndpointBy(String dataLocation) {
    switch (dataLocation) {
      case DATA_LOCATION_US: return ENDPOINT_US;
      case DATA_LOCATION_EU: return ENDPOINT_EU;
      default :
        LOGGER.w("Unsupported data location: " + dataLocation);
        return ENDPOINT_EU;
    }
  }

}
