package com.qubit.android.sdk.internal.configuration;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.Nullable;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.network.NetworkStateService;
import com.qubit.android.sdk.internal.util.DateTimeUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.qubit.android.sdk.internal.util.Elvis.*;

public class ConfigurationServiceImpl implements ConfigurationService {


  private static final QBLogger LOGGER = QBLogger.getFor("ConfigurationService");

  private static final String CONFIGURATION_URL = "https://s3-eu-west-1.amazonaws.com/";

  private final String trackingId;
  private final NetworkStateService networkStateService;
  private final ConfigurationRepository configurationRepository;
  private final ConfigurationConnector configurationConnector;

  private ConfigurationDownloadTask configurationDownloadTask = new ConfigurationDownloadTask();

  private Handler handler;

  private boolean isStarted = false;
  private Collection<ConfigurationListener> listeners = new CopyOnWriteArraySet<>();

  private ConfigurationModel currentConfiguration;
  private boolean isConnected;
  private Long lastUpdateAttemptTimestamp;


  public ConfigurationServiceImpl(String trackingId, NetworkStateService networkStateService,
                                  ConfigurationRepository configurationRepository) {
    this.trackingId = trackingId;
    this.networkStateService = networkStateService;
    this.configurationRepository = configurationRepository;

    configurationConnector = new Retrofit.Builder()
        .baseUrl(CONFIGURATION_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ConfigurationConnector.class);
  }

  @Override
  public void registerConfigurationListener(ConfigurationListener configurationListener) {
    listeners.add(configurationListener);
    if (currentConfiguration != null) {
      configurationListener.onConfigurationChange(currentConfiguration);
    }
  }

  public synchronized void start() {
    if (isStarted) {
      throw new IllegalStateException("ConfigurationService is already started");
    }

    HandlerThread thread = new HandlerThread("ConfigurationServiceThread", Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    handler = new Handler(thread.getLooper());

    handler.post(new InitialConfigurationLoadTask());

    networkStateService.registerNetworkStateListener(new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        handler.post(new NetworkStateChangeTask(isConnected));
      }
    });

    isStarted = true;
  }


  private class InitialConfigurationLoadTask implements Runnable {
    @Override
    public void run() {
      currentConfiguration = configurationRepository.load();
      if (currentConfiguration != null) {
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
        if (!downloadedConfiguration.equals(currentConfiguration)) {
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
    try {
      Response<ConfigurationResponse> response = configurationConnector.download(trackingId).execute();
      if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
        LOGGER.d("Configuration file not defined - the default one is used");
        return ConfigurationModel.getDefault();
      }
      if (!response.isSuccessful() || response.errorBody() != null || response.body() == null) {
        LOGGER.e("Failed to download configuration");
        return null;
      }

      ConfigurationResponse newConfiguration = response.body();
      LOGGER.d("Configuration downloaded");
      return enrichWithDefaultConfiguration(newConfiguration);
    } catch (IOException e) {
      LOGGER.e("Failed to download configuration: ", e);
      return null;
    }
  }

  private static ConfigurationModel enrichWithDefaultConfiguration(ConfigurationResponse newConfiguration) {
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

    return result;
  }

  private void notifyListenersConfigurationChange() {
    for (ConfigurationListener listener : listeners) {
      if (listener != null) {
        listener.onConfigurationChange(currentConfiguration);
      }
    }
  }

  private void scheduleNextConfigurationDownloadTask() {
    handler.removeCallbacks(configurationDownloadTask);
    if (!isConnected) {
      return;
    }
    long nextDownloadIntervalMs = evaluateNextConfigurationDownloadIntervalMs();
    if (nextDownloadIntervalMs > 0) {
      handler.postDelayed(configurationDownloadTask, nextDownloadIntervalMs);
      LOGGER.d("Next ConfigurationDownloadTask scheduled for " + nextDownloadIntervalMs);
    } else {
      handler.post(configurationDownloadTask);
      LOGGER.d("Next ConfigurationDownloadTask scheduled for NOW");
    }
  }

  private long evaluateNextConfigurationDownloadIntervalMs() {
    if (lastUpdateAttemptTimestamp == null && currentConfiguration == null) {
      return 0;
    }
    long reloadIntervalMs = DateTimeUtils.minToMs(currentConfiguration.getConfigurationReloadInterval());
    long baseTime = lastUpdateAttemptTimestamp != null
        ? lastUpdateAttemptTimestamp : currentConfiguration.getLastUpdateTimestamp();
    long nextDownloadTime = baseTime + reloadIntervalMs;
    long now = System.currentTimeMillis();
    return nextDownloadTime > now ? nextDownloadTime - now : 0;
  }

  private boolean isConfigurationUpToDate() {
    return currentConfiguration != null && currentConfiguration.getLastUpdateTimestamp() != null
        && currentConfiguration.getLastUpdateTimestamp()
        + DateTimeUtils.minToMs(currentConfiguration.getConfigurationReloadInterval())
        > System.currentTimeMillis();
  }

}
