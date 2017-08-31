package com.qubit.android.sdk.internal.eventtracker;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import com.qubit.android.sdk.api.tracker.EventTracker;
import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.internal.configuration.Configuration;
import com.qubit.android.sdk.internal.configuration.ConfigurationService;
import com.qubit.android.sdk.internal.eventtracker.repository.EventModel;
import com.qubit.android.sdk.internal.eventtracker.repository.EventsRepository;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.network.NetworkStateService;
import com.qubit.android.sdk.internal.util.ListUtil;
import com.qubit.android.sdk.internal.util.Uninterruptibles;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventTrackerImpl implements EventTracker {

  private static final QBLogger LOGGER = QBLogger.getFor("EventTracker");

  private static final int BATCH_MAX_SIZE = 15;
  private static final int BATCH_INTERVAL_MS = 500;
  private static final int SEND_EVENT_TIME_MS = 300;

  private final Context context;
  private final ConfigurationService configurationService;
  private final NetworkStateService networkStateService;
  private final EventsRepository eventsRepository;

  private final SendEventsTask sendEventsTask = new SendEventsTask();

  private Handler handler;

  private boolean isStarted = false;
  private boolean isEnabled = true;

  private Configuration currentConfiguration = null;
  private boolean isConnected = false;

  private Integer queueSize = null;
  private Long firstEventInBatchTime = null;

  public EventTrackerImpl(Context context, ConfigurationService configurationService,
                          NetworkStateService networkStateService, EventsRepository eventsRepository) {
    this.context = context;
    this.configurationService = configurationService;
    this.networkStateService = networkStateService;
    this.eventsRepository = eventsRepository;
  }

  @Override
  public synchronized void sendEvent(String type, QBEvent event) {
    if (isStarted && isEnabled) {
      handler.post(new StoreEventTask(type, event));
    }
  }

  @Override
  public synchronized void enable(boolean enable) {
    isEnabled = enable;
  }

  public synchronized void start() {
    if (isStarted) {
      throw new IllegalStateException("EventTracker is already started");
    }
    HandlerThread thread = new HandlerThread("EventTrackerThread", Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    handler = new Handler(thread.getLooper());

    configurationService.registerConfigurationListener(new ConfigurationService.ConfigurationListener() {
      @Override
      public void onInitialization(Configuration configuration) {
        handler.post(new ConfigurationInitTask(configuration));
      }

      @Override
      public void onConfigurationChange(Configuration configuration) {
        handler.post(new ConfigurationChangeTask(configuration));
      }
    });

    networkStateService.registerNetworkStateListener(new NetworkStateService.NetworkStateListener() {
      @Override
      public void onNetworkStateChange(boolean isConnected) {
        handler.post(new NetworkStateChangeTask(isConnected));
      }
    });
    isStarted = true;
  }


  private class StoreEventTask implements Runnable {
    private final String type;
    private final QBEvent qbEvent;

    StoreEventTask(String type, QBEvent qbEvent) {
      this.type = type;
      this.qbEvent = qbEvent;
    }

    @Override
    public void run() {
      LOGGER.d("Storing event");
      eventsRepository.insert(type, qbEvent.toJsonObject().toString());
      queueSize = queueSize != null ? queueSize + 1 : 1;
      if (firstEventInBatchTime == null) {
        firstEventInBatchTime = System.currentTimeMillis();
      }
      scheduleNextSendEventsTask();
    }
  }

  private class ConfigurationInitTask implements Runnable {
    private final Configuration configuration;

    ConfigurationInitTask(Configuration configuration) {
      this.configuration = configuration;
    }

    @Override
    public void run() {
      LOGGER.d("Configuration Initialized");
      currentConfiguration = configuration;
      scheduleNextSendEventsTask();
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
      EventTrackerImpl.this.isConnected = isConnected;
      scheduleNextSendEventsTask();
    }
  }

  private class SendEventsTask implements Runnable {

    @Override
    public void run() {
      LOGGER.d("Send events task");

      if (currentConfiguration == null) {
        LOGGER.d("Configuration is not initialized yet");
        return;
      }

      Long timeMsToSendEvents = evaluateTimeMsToNextSendEvents();
      if (timeMsToSendEvents == null) {
        LOGGER.d("SendEventsTask: No events in queue");
        return;
      }

      if (timeMsToSendEvents > 0) {
        LOGGER.d("SendEventsTask: Batch is not full. Postponing sending events by "
            + timeMsToSendEvents + " ms.");
        handler.postDelayed(sendEventsTask, timeMsToSendEvents);
        return;
      }

      List<EventModel> nextEvents = eventsRepository.selectFirst(BATCH_MAX_SIZE + 1);

      List<EventModel> eventsToSent = ListUtil.firstElements(nextEvents, BATCH_MAX_SIZE);
      LOGGER.d("SendEventTask: Sending events: " + eventsToSent.size());
      // TODO send 15 events, delete them
      Uninterruptibles.sleepUninterruptibly(SEND_EVENT_TIME_MS, TimeUnit.MILLISECONDS);
      // TODO negative path
      eventsRepository.delete(extractEventsIds(eventsToSent));
      queueSize -= eventsToSent.size();
      firstEventInBatchTime = nextEvents.size() > eventsToSent.size()
          ? nextEvents.get(eventsToSent.size()).getCreationTimestamp() : null;

      scheduleNextSendEventsTask();
    }
  }

  private void scheduleNextSendEventsTask() {
    handler.removeCallbacks(sendEventsTask);
    if (!isConnected || currentConfiguration == null) {
      return;
    }

    Long timeMsToNextSendEvents = evaluateTimeMsToNextSendEvents();
    if (timeMsToNextSendEvents != null) {
      if (timeMsToNextSendEvents > 0) {
        handler.postDelayed(sendEventsTask, timeMsToNextSendEvents);
        LOGGER.d("Next SendEventsTask scheduled for " + timeMsToNextSendEvents);
      } else {
        handler.post(sendEventsTask);
        LOGGER.d("Next SendEventsTask scheduled for NOW");
      }
    }
  }

  /**
   * @return null - Nothing to send. 0 - Now. N - miliseconds to next send events task
   */
  private Long evaluateTimeMsToNextSendEvents() {
    if (queueSize == null) {
      queueSize = eventsRepository.countEvents();
    }
    if (queueSize == 0) {
      return null;
    }
    if (queueSize >= BATCH_MAX_SIZE) {
      return 0L;
    }
    if (firstEventInBatchTime == null) {
      LOGGER.w("INCONSISTENCY! FirstEventInBatchTime is null, while there are events in queue");
      // TODO fix cache
      return null;
    }
    long now = System.currentTimeMillis();
    long nextSendEventsTime = firstEventInBatchTime + BATCH_INTERVAL_MS;
    return (nextSendEventsTime > now) ? nextSendEventsTime - now : 0L;
  }

  private static Collection<Long> extractEventsIds(Collection<EventModel> events) {
    HashSet<Long> ids = new HashSet<>(events.size());
    for (EventModel event : events) {
      ids.add(event.getId());
    }
    return ids;
  }
}
