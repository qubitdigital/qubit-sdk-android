package com.qubit.android.sdk.internal.configuration;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.qubit.android.sdk.internal.network.NetworkStateService;
import com.qubit.android.sdk.internal.util.DateTimeUtils;
import com.qubit.android.sdk.internal.util.Elvis;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigurationServiceImpl implements ConfigurationService {

  private static final String LOG_TAG = "qb-sdk";
  private static final String CONFIGURATION_URL = "https://s3-eu-west-1.amazonaws.com/";

  private final Context context;
  private final String trackingId;
  private final NetworkStateService networkStateService;
  private final ConfigurationRepository configurationRepository;
  private final ConfigurationConnector configurationConnector;

  private ConfigurationChangeTask configurationChangeTask = new ConfigurationChangeTask();

  private Handler handler;

  private boolean isStarted = false;
  private Collection<ConfigurationListener> listeners = new CopyOnWriteArraySet<>();

  public ConfigurationServiceImpl(Context context, String trackingId, NetworkStateService networkStateService,
                                  ConfigurationRepository configurationRepository) {
    this.context = context;
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
    if (getConfiguration() != null) {
      notifyListenerInitialization(configurationListener);
    }
  }

  public Configuration getConfiguration() {
    return configurationRepository.load(context);
  }

  public synchronized void start() {
    if (isStarted) {
      throw new IllegalStateException("ConfigurationService is already started");
    }

    HandlerThread thread = new HandlerThread("ConfigurationService", Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    handler = new Handler(thread.getLooper());

    networkStateService.registerNetworkStateListener(new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        handler.post(new NetworkStateChangeTask(isConnected));
      }
    });

    isStarted = true;
  }

  private class NetworkStateChangeTask implements Runnable {
    private final boolean isConnected;

    NetworkStateChangeTask(boolean connected) {
      this.isConnected = connected;
    }

    @Override
    public void run() {
      Log.d(LOG_TAG, "ConfigurationService: Network state changed. Connected: " + isConnected);
      if (isConnected) {
        Configuration configuration = getConfiguration();
        if (isConfigurationUpToDate(configuration)) {
          return;
        }

        if (configuration != null) {
          handler.post(configurationChangeTask);
        } else {
          handler.post(new ConfigurationInitializationTask());
        }
      }
    }

    private boolean isConfigurationUpToDate(Configuration configuration) {
      return configuration != null && configuration.getLastUpdateTimestamp() != null
          && configuration.getLastUpdateTimestamp()
          + DateTimeUtils.minToMs(configuration.getConfigurationReloadInterval())
          > System.currentTimeMillis();
    }
  }

  private class ConfigurationInitializationTask implements Runnable {

    @Override
    public void run() {
      Log.d(LOG_TAG, "ConfigurationService: first time downloading configuration");
      Configuration configuration = downloadConfiguration();
      if (configuration != null) {
        notifyListenersInitialization();
      } else {
        configuration = ConfigurationImpl.getDefault();
      }
      handler.postDelayed(configurationChangeTask,
          DateTimeUtils.minToMs(configuration.getConfigurationReloadInterval()));

    }
  }

  private class ConfigurationChangeTask implements Runnable {

    @Override
    public void run() {
      Log.d(LOG_TAG, "ConfigurationService: downloading configuration");
      Configuration configuration = downloadConfiguration();
      if (configuration != null) {
        notifyListenersConfigurationChange();
      } else {
        configuration = ConfigurationImpl.getDefault();
      }
      handler.postDelayed(configurationChangeTask,
          DateTimeUtils.minToMs(configuration.getConfigurationReloadInterval()));
    }
  }

  @Nullable
  private Configuration downloadConfiguration() {
    handler.removeCallbacks(configurationChangeTask);
    try {
      Response<ConfigurationResponse> response = configurationConnector.download(trackingId).execute();
      if (!response.isSuccessful() || response.errorBody() != null || response.body() == null) {
        Log.e(LOG_TAG, "ConfigurationService: failed to download configuration");
        return null;
      }

      ConfigurationResponse newConfiguration = response.body();
      ConfigurationImpl currentConfiguration = getCurrentConfiguration();
      ConfigurationImpl resultConfiguration = createMergedConfiguration(currentConfiguration, newConfiguration);

      resultConfiguration.setLastUpdateTimestamp(System.currentTimeMillis());
      configurationRepository.save(context, resultConfiguration);

      Log.d(LOG_TAG, "ConfigurationService: configuration: " + resultConfiguration);
      return resultConfiguration;
    } catch (IOException e) {
      Log.e(LOG_TAG, "ConfigurationService: failed to download configuration: " + e.getMessage());
      return null;
    }
  }

  @NonNull
  private ConfigurationImpl getCurrentConfiguration() {
    ConfigurationImpl currentConfiguration = (ConfigurationImpl) getConfiguration();
    if (currentConfiguration == null) {
      currentConfiguration = ConfigurationImpl.getDefault();
    }
    return currentConfiguration;
  }

  private ConfigurationImpl createMergedConfiguration(ConfigurationImpl baseConfiguration,
                                                      ConfigurationResponse newConfiguration) {
    ConfigurationImpl resultConfiguration = new ConfigurationImpl();

    resultConfiguration.setEndpoint(
        Elvis.getNotEmpty(newConfiguration.getEndpoint(), baseConfiguration.getEndpoint()));
    resultConfiguration.setDataLocation(
        Elvis.getNotEmpty(newConfiguration.getDataLocation(), baseConfiguration.getDataLocation()));
    resultConfiguration.setConfigurationReloadInterval(
        Elvis.get(
            newConfiguration.getConfigurationReloadInterval(),
            baseConfiguration.getConfigurationReloadInterval()));
    resultConfiguration.setQueueTimeout(
        Elvis.get(newConfiguration.getQueueTimeout(), baseConfiguration.getQueueTimeout()));
    resultConfiguration.setVertical(
        Elvis.getNotEmpty(newConfiguration.getVertical(), baseConfiguration.getVertical()));
    resultConfiguration.setNamespace(
        Elvis.getNotEmpty(newConfiguration.getNamespace(), baseConfiguration.getNamespace()));
    resultConfiguration.setPropertyId(
        Elvis.get(newConfiguration.getPropertyId(), baseConfiguration.getPropertyId()));
    resultConfiguration.setDisabled(
        Elvis.get(newConfiguration.isDisabled(), baseConfiguration.isDisabled()));
    resultConfiguration.setLookupAttributeUrl(
        Elvis.getNotEmpty(newConfiguration.getLookupAttributeUrl(), baseConfiguration.getLookupAttributeUrl()));
    resultConfiguration.setLookupGetRequestTimeout(
        Elvis.get(newConfiguration.getLookupGetRequestTimeout(), baseConfiguration.getLookupGetRequestTimeout()));
    resultConfiguration.setLookupCacheExpireTime(
        Elvis.get(newConfiguration.getLookupCacheExpireTime(), baseConfiguration.getLookupCacheExpireTime()));

    return resultConfiguration;
  }

  private void notifyListenerInitialization(ConfigurationListener listener) {
    if (listener != null) {
      listener.onInitialization(getConfiguration());
    }
  }

  private void notifyListenersInitialization() {
    for (ConfigurationListener listener : listeners) {
      if (listener != null) {
        listener.onInitialization(getConfiguration());
      }
    }
  }

  private void notifyListenersConfigurationChange() {
    for (ConfigurationListener listener : listeners) {
      if (listener != null) {
        listener.onConfigurationChange(getConfiguration());
      }
    }
  }

}
