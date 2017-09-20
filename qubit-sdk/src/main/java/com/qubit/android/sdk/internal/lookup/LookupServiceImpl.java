package com.qubit.android.sdk.internal.lookup;

import com.qubit.android.sdk.internal.common.service.QBService;
import com.qubit.android.sdk.internal.configuration.Configuration;
import com.qubit.android.sdk.internal.configuration.ConfigurationService;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.lookup.connector.LookupConnector;
import com.qubit.android.sdk.internal.lookup.connector.LookupConnectorBuilder;
import com.qubit.android.sdk.internal.lookup.model.LookupModel;
import com.qubit.android.sdk.internal.lookup.repository.LookupCache;
import com.qubit.android.sdk.internal.lookup.repository.LookupRepository;
import com.qubit.android.sdk.internal.network.NetworkStateService;
import com.qubit.android.sdk.internal.util.DateTimeUtils;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;


public class LookupServiceImpl extends QBService implements LookupService {

  private static final String SERVICE_NAME = "LookupService";
  private static final QBLogger LOGGER = QBLogger.getFor(SERVICE_NAME);

  private static final int EXP_BACKOFF_BASE_TIME_SECS = 1;
  private static final int EXP_BACKOFF_MAX_SENDING_ATTEMPTS = 7;
  private static final int MAX_RETRY_INTERVAL_SECS = 60 * 5;

  private final ConfigurationService configurationService;
  private final NetworkStateService networkStateService;
  private final LookupRepository lookupRepository;
  private final LookupConnectorBuilder lookupConnectorBuilder;

  private final ConfigurationService.ConfigurationListener configurationListener;
  private final NetworkStateService.NetworkStateListener networkStateListener;
  private LookupRequestTask lookupRequestTask = new LookupRequestTask();
  private SetDefaultLookupTask setDefaultLookupTask = new SetDefaultLookupTask();

  private Collection<LookupListener> listeners = new CopyOnWriteArraySet<>();
  private LookupConnector lookupConnector = null;

  private long initTime;
  private LookupCache currentLookupCache = null;
  private int requestAttempts = 0;
  private long lastAttemptTime = 0;

  private Configuration currentConfiguration = null;
  private boolean isConnected = false;


  public LookupServiceImpl(
                           ConfigurationService configurationService, NetworkStateService networkStateService,
                           LookupRepository lookupRepository, LookupConnectorBuilder lookupConnectorBuilder) {
    super(SERVICE_NAME);
    this.configurationService = configurationService;
    this.networkStateService = networkStateService;
    this.lookupRepository = lookupRepository;
    this.lookupConnectorBuilder = lookupConnectorBuilder;
    configurationListener = new ConfigurationService.ConfigurationListener() {
      @Override
      public void onConfigurationChange(Configuration configuration) {
        postTask(new ConfigurationChangeTask(configuration));
      }
    };
    networkStateListener = new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        postTask(new NetworkStateChangeTask(isConnected));
      }
    };

  }

  @Override
  protected void onStart() {
    postTask(new InitialLookupLoadTask());
    configurationService.registerConfigurationListener(configurationListener);
    networkStateService.registerNetworkStateListener(networkStateListener);
  }

  @Override
  protected void onStop() {
    configurationService.unregisterConfigurationListener(configurationListener);
    networkStateService.unregisterNetworkStateListener(networkStateListener);
  }

  @Override
  public void registerLookupListener(final LookupListener listener) {
    postTask(new Runnable() {
      @Override
      public void run() {
        listeners.add(listener);
        if (currentLookupCache != null && currentLookupCache.getLookupModel() != null) {
          notifyLookupDataChange(listener, currentLookupCache.getLookupModel());
        }
      }
    });

  }

  @Override
  public void unregisterLookupListener(final LookupListener listener) {
    postTask(new Runnable() {
      @Override
      public void run() {
        listeners.remove(listener);
      }
    });
  }

  private class InitialLookupLoadTask implements Runnable {
    @Override
    public void run() {
      initTime = System.currentTimeMillis();
      currentLookupCache = lookupRepository.load();
      if (currentLookupCache != null) {
        LOGGER.d("Lookup loaded from local storage");
        notifyListenersLookupDataChange();
      } else {
        scheduleSetDefaultLookupTask();
      }
      scheduleNextLookupRequestTask();
    }
  }

  private class ConfigurationChangeTask implements Runnable {
    private final Configuration configuration;

    ConfigurationChangeTask(Configuration configuration) {
      this.configuration = configuration;
    }

    @Override
    public void run() {
      LOGGER.d("Configuration Changed");
      currentConfiguration = configuration;
      try {
        lookupConnector = lookupConnectorBuilder.buildFor(currentConfiguration.getLookupAttributeUrl());
        clearAttempts();
        scheduleNextLookupRequestTask();
        scheduleSetDefaultLookupTask();
      } catch (IllegalArgumentException e) {
        LOGGER.e("Cannot create Rest API connector. Most likely endpoint url is incorrect.", e);
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
      LookupServiceImpl.this.isConnected = isConnected;
      if (isConnected) {
        clearAttempts();
      }
      scheduleNextLookupRequestTask();
    }
  }

  private class LookupRequestTask implements Runnable {

    @Override
    public void run() {
      LOGGER.d("Requesting lookup");
      if (isLookupUpToDate()) {
        scheduleNextLookupRequestTask();
        return;
      }
      if (lookupConnector == null) {
        LOGGER.d("Lookup connector is not defined yet.");
      }
      if (!isConnected) {
        return;
      }

      LookupModel newLookupModel = lookupConnector.getLookupData();
      if (newLookupModel != null) {
        registerSuccessfulAttempt();
        currentLookupCache = new LookupCache(newLookupModel, System.currentTimeMillis());
        lookupRepository.save(currentLookupCache);
        LOGGER.d("New lookup downloaded: " + newLookupModel);
        notifyListenersLookupDataChange();
      } else {
        registerFailedAttempt();
        LOGGER.d("New lookup request failed. Current lookup: " + currentLookupCache);
      }

      scheduleNextLookupRequestTask();
    }
  }

  private class SetDefaultLookupTask implements Runnable {
    @Override
    public void run() {
      LOGGER.d("SetDefaultLookupTask");
      if (currentLookupCache != null) {
        return;
      }
      currentLookupCache = LookupCache.EMPTY;
      LOGGER.d("Default empty lookup data set");
      notifyListenersLookupDataChange();
    }
  }

  private void notifyListenersLookupDataChange() {
    LOGGER.d("Sending event lookup data change");
    for (LookupListener listener : listeners) {
      notifyLookupDataChange(listener, currentLookupCache.getLookupModel());
    }
  }

  private static void notifyLookupDataChange(LookupListener listener, LookupData lookupData) {
    if (listener != null) {
      listener.onLookupDataChange(lookupData);
    }
  }

  private void scheduleSetDefaultLookupTask() {
    removeTask(setDefaultLookupTask);
    if (currentLookupCache != null || currentConfiguration == null) {
      return;
    }

    long setDefaultTime = initTime + DateTimeUtils.secToMs(currentConfiguration.getLookupGetRequestTimeout());
    long now = System.currentTimeMillis();
    long timeToSetDefault = setDefaultTime > now ? setDefaultTime - now : 0;
    postTaskDelayed(setDefaultLookupTask, timeToSetDefault);
    LOGGER.d("SetDefaultLookupTask scheduled for " + timeToSetDefault);
  }

  private void scheduleNextLookupRequestTask() {
    removeTask(lookupRequestTask);
    if (!isConnected || currentConfiguration == null || lookupConnector == null) {
      return;
    }

    long timeMsToNextRequest = requestAttempts > 0
        ? evaluateTimeMsToNextRetry()
        : evaluateTimeMsToExpiration();

    if (timeMsToNextRequest > 0) {
      postTaskDelayed(lookupRequestTask, timeMsToNextRequest);
      LOGGER.d("Next LookupRequestTask scheduled for " + timeMsToNextRequest);
    } else {
      postTask(lookupRequestTask);
      LOGGER.d("Next LookupRequestTask scheduled for NOW");
    }
  }

  private long evaluateTimeMsToExpiration() {
    if (currentLookupCache == null) {
      return 0;
    }
    long lookupExpiryTimeMs = DateTimeUtils.minToMs(currentConfiguration.getLookupCacheExpireTime());
    long nextDownloadTime = currentLookupCache.getLastUpdateTimestamp() + lookupExpiryTimeMs;
    long now = System.currentTimeMillis();
    return nextDownloadTime > now ? nextDownloadTime - now : 0;
  }

  private void registerSuccessfulAttempt() {
    clearAttempts();
  }

  private void clearAttempts() {
    requestAttempts = 0;
    lastAttemptTime = 0;
  }

  private void registerFailedAttempt() {
    requestAttempts++;
    lastAttemptTime = System.currentTimeMillis();
  }

  private long evaluateTimeMsToNextRetry() {
    long nextRetryIntervalMs = DateTimeUtils.secToMs(evaluateIntervalSecsToNextRetry(requestAttempts));
    long nextRetryTimeMs = lastAttemptTime + nextRetryIntervalMs;
    long now = System.currentTimeMillis();
    return Math.max(nextRetryTimeMs - now, 0);
  }

  private static long evaluateIntervalSecsToNextRetry(int sendingAttemptsDone) {
    if (sendingAttemptsDone > EXP_BACKOFF_MAX_SENDING_ATTEMPTS) {
      return MAX_RETRY_INTERVAL_SECS;
    } else {
      return (1L << (sendingAttemptsDone - 1)) * EXP_BACKOFF_BASE_TIME_SECS;
    }
  }

  private boolean isLookupUpToDate() {
    return currentLookupCache != null
        && currentLookupCache.getLastUpdateTimestamp()
        + DateTimeUtils.minToMs(currentConfiguration.getLookupCacheExpireTime())
        > System.currentTimeMillis();
  }

}
