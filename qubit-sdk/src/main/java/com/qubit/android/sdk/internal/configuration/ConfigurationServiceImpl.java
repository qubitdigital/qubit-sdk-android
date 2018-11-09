package com.qubit.android.sdk.internal.configuration;

import com.qubit.android.sdk.internal.common.service.QBService;
import com.qubit.android.sdk.internal.configuration.connector.ConfigurationConnector;
import com.qubit.android.sdk.internal.configuration.connector.ConfigurationConnectorBuilder;
import com.qubit.android.sdk.internal.configuration.connector.ConfigurationResponse;
import com.qubit.android.sdk.internal.configuration.connector.ConfigurationRestModel;
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationModel;
import com.qubit.android.sdk.internal.configuration.repository.ConfigurationRepository;
import com.qubit.android.sdk.internal.common.logging.QBLogger;
import com.qubit.android.sdk.internal.network.NetworkStateService;
import com.qubit.android.sdk.internal.common.util.DateTimeUtils;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.qubit.android.sdk.internal.common.util.Elvis.*;

public class ConfigurationServiceImpl extends QBService implements ConfigurationService {

  public static final String configurationUrl = "https://s3-eu-west-1.amazonaws.com/qubit-mobile-config/";
  public static boolean enforceDownloadOnStart = false;

  private static final String SERVICE_NAME = "ConfigurationService";

  private static final QBLogger LOGGER = QBLogger.getFor(SERVICE_NAME);

  private final NetworkStateService networkStateService;
  private final ConfigurationRepository configurationRepository;
  private final ConfigurationDownloadTask configurationDownloadTask = new ConfigurationDownloadTask();
  private final NetworkStateService.NetworkStateListener networkStateListener;
  private final ConfigurationConnectorBuilder configurationConnectorBuilder;

  private ConfigurationConnector configurationConnector;

  private Collection<ConfigurationListener> listeners = new CopyOnWriteArraySet<>();
  private ConfigurationModel currentConfiguration;
  private boolean isConnected;
  private Long lastUpdateAttemptTimestamp;


  public ConfigurationServiceImpl(NetworkStateService networkStateService,
                                  ConfigurationRepository configurationRepository,
                                  ConfigurationConnectorBuilder configurationConnectorBuilder) {
    super(SERVICE_NAME);
    this.networkStateService = networkStateService;
    this.configurationRepository = configurationRepository;
    this.configurationConnectorBuilder = configurationConnectorBuilder;
    networkStateListener = new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        postTask(new NetworkStateChangeTask(isConnected));
      }
    };
  }

  @Override
  protected void onStart() {
    postTask(new InitialConfigurationLoadTask());
    networkStateService.registerNetworkStateListener(networkStateListener);
  }

  @Override
  protected void onStop() {
    networkStateService.unregisterNetworkStateListener(networkStateListener);
  }

  @Override
  public void registerConfigurationListener(final ConfigurationListener configurationListener) {
    postTask(new Runnable() {
      @Override
      public void run() {
        listeners.add(configurationListener);
        if (currentConfiguration != null) {
          notifyConfigurationChange(configurationListener, currentConfiguration);
        }
      }
    });
  }

  @Override
  public void unregisterConfigurationListener(final ConfigurationListener configurationListener) {
    postTask(new Runnable() {
      @Override
      public void run() {
        listeners.remove(configurationListener);
      }
    });
  }


  private class InitialConfigurationLoadTask implements Runnable {
    @Override
    public void run() {
      currentConfiguration = configurationRepository.load();
      if (currentConfiguration != null) {
        LOGGER.d("Configuration loaded from local storage");
        if (enforceDownloadOnStart) {
          currentConfiguration.setLastUpdateTimestamp(null);
        }
        scheduleNextConfigurationDownloadTask();
        notifyListenersConfigurationChange();
      }
    }
  }

  private class NetworkStateChangeTask implements Runnable {
    private final boolean isConnected;

    NetworkStateChangeTask(boolean connected) {
      this.isConnected = connected;
    }

    @Override
    public void run() {
      LOGGER.d("Network state changed. Connected: " + isConnected);
      ConfigurationServiceImpl.this.isConnected = isConnected;
      scheduleNextConfigurationDownloadTask();
    }
  }

  private class ConfigurationDownloadTask implements Runnable {

    @Override
    public void run() {
      LOGGER.d("Downloading configuration");
      if (isConfigurationUpToDate()) {
        scheduleNextConfigurationDownloadTask();
        return;
      }
      if (!isConnected) {
        return;
      }

      ConfigurationModel downloadedConfiguration = downloadConfiguration();
      long now = System.currentTimeMillis();
      lastUpdateAttemptTimestamp = now;
      if (downloadedConfiguration != null) {
        downloadedConfiguration.setLastUpdateTimestamp(now);
        configurationRepository.save(downloadedConfiguration);
        if (!downloadedConfiguration.equalsIgnoreLastUpdateTimestamp(currentConfiguration)) {
          currentConfiguration = downloadedConfiguration;
          notifyListenersConfigurationChange();
          LOGGER.d("Configuration data changed");
        }
      }
      LOGGER.d("Current configuration: " + getIfNotNull(currentConfiguration, "none"));

      scheduleNextConfigurationDownloadTask();
    }
  }

  @Nullable
  private ConfigurationModel downloadConfiguration() {
    ConfigurationResponse response = getConfigurationConnector().download();
    switch (response.getStatus()) {
      case OK :
        return enrichWithDefaultConfiguration(response.getConfiguration());
      case NOT_FOUND:
        LOGGER.d("Configuration file not defined - the default one is used");
        return ConfigurationModel.getDefault();
      default:
        return null;
    }
  }

  private ConfigurationConnector getConfigurationConnector() {
    if (configurationConnector == null) {
      configurationConnector = configurationConnectorBuilder.buildFor(configurationUrl);
    }
    return configurationConnector;
  }

  private static ConfigurationModel enrichWithDefaultConfiguration(ConfigurationRestModel newConfiguration) {
    ConfigurationModel defaultConf = ConfigurationModel.getDefault();
    ConfigurationModel result = new ConfigurationModel();

    String dataLocation = getIfNotEmpty(newConfiguration.getDataLocation(), defaultConf.getDataLocation());
    result.setDataLocation(dataLocation);

    result.setEndpoint(getIfNotEmpty(newConfiguration.getEndpoint(), ConfigurationModel.getEndpointBy(dataLocation)));

    result.setConfigurationReloadInterval(
        getIfNotNull(
            newConfiguration.getConfigurationReloadInterval(),
            defaultConf.getConfigurationReloadInterval()));
    result.setQueueTimeout(
        getIfNotNull(newConfiguration.getQueueTimeout(), defaultConf.getQueueTimeout()));
    result.setVertical(
        getIfNotEmpty(newConfiguration.getVertical(), defaultConf.getVertical()));
    result.setNamespace(
        getIfNotEmpty(newConfiguration.getNamespace(), defaultConf.getNamespace()));
    result.setPropertyId(
        getIfNotNull(newConfiguration.getPropertyId(), defaultConf.getPropertyId()));
    result.setDisabled(
        getIfNotNull(newConfiguration.isDisabled(), defaultConf.isDisabled()));
    result.setLookupAttributeUrl(
        getIfNotEmpty(newConfiguration.getLookupAttributeUrl(), defaultConf.getLookupAttributeUrl()));
    result.setLookupGetRequestTimeout(
        getIfNotNull(newConfiguration.getLookupGetRequestTimeout(), defaultConf.getLookupGetRequestTimeout()));
    result.setLookupCacheExpireTime(
        getIfNotNull(newConfiguration.getLookupCacheExpireTime(), defaultConf.getLookupCacheExpireTime()));
    result.setExperienceApiHost(
        getIfNotEmpty(newConfiguration.getExperienceApiHost(), defaultConf.getExperienceApiHost()));
    result.setExperienceApiCacheExpireTime(
        getIfNotNull(newConfiguration.getExperienceApiCacheExpireTime(), defaultConf.getExperienceApiCacheExpireTime()));

    return result;
  }

  private void notifyListenersConfigurationChange() {
    for (ConfigurationListener listener : listeners) {
      notifyConfigurationChange(listener, currentConfiguration);
    }
  }

  private static void notifyConfigurationChange(ConfigurationListener listener, Configuration configuration) {
    if (listener != null) {
      listener.onConfigurationChange(configuration);
    }
  }

  private void scheduleNextConfigurationDownloadTask() {
    removeTask(configurationDownloadTask);
    if (!isConnected) {
      return;
    }
    long nextDownloadIntervalMs = evaluateNextConfigurationDownloadIntervalMs();
    if (nextDownloadIntervalMs > 0) {
      postTaskDelayed(configurationDownloadTask, nextDownloadIntervalMs);
      LOGGER.d("Next ConfigurationDownloadTask scheduled for " + nextDownloadIntervalMs);
    } else {
      postTask(configurationDownloadTask);
      LOGGER.d("Next ConfigurationDownloadTask scheduled for NOW");
    }
  }

  private long evaluateNextConfigurationDownloadIntervalMs() {
    if (lastUpdateAttemptTimestamp == null
        && (currentConfiguration == null || currentConfiguration.getLastUpdateTimestamp() == null)) {
      return 0;
    }
    long reloadIntervalMs = getConfigurationReloadIntervalMs();
    long baseTime = lastUpdateAttemptTimestamp != null
        ? lastUpdateAttemptTimestamp : currentConfiguration.getLastUpdateTimestamp();
    long nextDownloadTime = baseTime + reloadIntervalMs;
    long now = System.currentTimeMillis();
    return nextDownloadTime > now ? nextDownloadTime - now : 0;
  }

  private long getConfigurationReloadIntervalMs() {
    ConfigurationModel effectiveConfiguration =
        currentConfiguration != null ? currentConfiguration : ConfigurationModel.getDefault();
    return DateTimeUtils.minToMs(effectiveConfiguration.getConfigurationReloadInterval());
  }

  private boolean isConfigurationUpToDate() {
    return currentConfiguration != null && currentConfiguration.getLastUpdateTimestamp() != null
        && currentConfiguration.getLastUpdateTimestamp()
        + DateTimeUtils.minToMs(currentConfiguration.getConfigurationReloadInterval())
        > System.currentTimeMillis();
  }

}
