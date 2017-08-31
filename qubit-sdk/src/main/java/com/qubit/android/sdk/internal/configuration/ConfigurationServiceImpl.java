package com.qubit.android.sdk.internal.configuration;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import com.qubit.android.sdk.internal.network.NetworkStateService;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConfigurationServiceImpl implements ConfigurationService {

  private final Context context;
  private final String trackingId;
  private final NetworkStateService networkStateService;
  private final ConfigurationRepository configurationRepository;

  private HandlerThread thread;
  private Handler handler;

  private boolean isStarted = false;
  private Collection<ConfigurationListener> listeners = new CopyOnWriteArraySet<>();
  private ConfigurationImpl configuration;

  private final Runnable initializationTask = new Runnable() {
    @Override
    public void run() {
      configuration = new ConfigurationImpl();
      notifyListenersInitialization(configuration);
      handler.postDelayed(configurationChangeTask, 60000);
    }
  };

  private final Runnable configurationChangeTask = new Runnable() {
    @Override
    public void run() {
      configuration = new ConfigurationImpl();
      notifyListenersConfigurationChange(configuration);
      handler.postDelayed(configurationChangeTask, 60000);
    }
  };

  public ConfigurationServiceImpl(Context context, String trackingId, NetworkStateService networkStateService,
                                  ConfigurationRepository configurationRepository) {
    this.context = context;
    this.trackingId = trackingId;
    this.networkStateService = networkStateService;
    this.configurationRepository = configurationRepository;
  }

  @Override
  public void registerConfigurationListener(ConfigurationListener listener) {
    listeners.add(listener);
  }

  @Override
  public boolean isInitialized() {
    return configuration != null;
  }

  @Override
  public Configuration getConfiguration() {
    return null;
  }

  public synchronized void start() {
    if (isStarted) {
      throw new IllegalStateException("ConfigurationService is already started");
    }
    thread = new HandlerThread("ConfigurationService", Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    handler = new Handler(thread.getLooper());

    networkStateService.registerNetworkStateListener(new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        // TODO
      }
    });
    isStarted = true;

    handler.postDelayed(initializationTask, 10000);
  }


  private void notifyListenersInitialization(Configuration configuration) {
    for (ConfigurationListener listener : listeners) {
      listener.onInitialization(configuration);
    }
  }

  private void notifyListenersConfigurationChange(Configuration configuration) {
    for (ConfigurationListener listener : listeners) {
      listener.onConfigurationChange(configuration);
    }
  }

}
