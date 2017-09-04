package com.qubit.android.sdk.internal.eventtracker;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import com.qubit.android.sdk.api.tracker.EventTracker;
import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.internal.configuration.Configuration;
import com.qubit.android.sdk.internal.configuration.ConfigurationService;
import com.qubit.android.sdk.internal.eventtracker.connector.EventRestModel;
import com.qubit.android.sdk.internal.eventtracker.connector.EventsRestAPIConnector;
import com.qubit.android.sdk.internal.eventtracker.connector.EventsRestAPIConnectorBuilder;
import com.qubit.android.sdk.internal.eventtracker.repository.CachingEventsRepository;
import com.qubit.android.sdk.internal.eventtracker.repository.EventModel;
import com.qubit.android.sdk.internal.eventtracker.repository.EventsRepository;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.network.NetworkStateService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class EventTrackerImpl implements EventTracker {

  private static final QBLogger LOGGER = QBLogger.getFor("EventTracker");

  private static final int BATCH_MAX_SIZE = 15;
  private static final int BATCH_INTERVAL_MS = 500;

  private final ConfigurationService configurationService;
  private final NetworkStateService networkStateService;
  private final EventsRepository eventsRepository;
  private final EventsRestAPIConnectorBuilder eventsRestAPIConnectorBuilder;
  private final EventRestModelCreator eventRestModelCreator;
  private final SendEventsTask sendEventsTask = new SendEventsTask();

  private Handler handler;

  private boolean isStarted = false;
  private boolean isEnabled = true;

  private Configuration currentConfiguration = null;
  private boolean isConnected = false;
  private EventsRestAPIConnector apiConnector = null;

  public EventTrackerImpl(String trackingId, String deviceId,
                          ConfigurationService configurationService,
                          NetworkStateService networkStateService, EventsRepository eventsRepository,
                          EventsRestAPIConnectorBuilder eventsRestAPIConnectorBuilder) {
    this.configurationService = configurationService;
    this.networkStateService = networkStateService;
    this.eventsRepository = new CachingEventsRepository(eventsRepository);
    this.eventsRestAPIConnectorBuilder = eventsRestAPIConnectorBuilder;
    eventRestModelCreator = new EventRestModelCreator(trackingId, deviceId);
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
      try {
        apiConnector = eventsRestAPIConnectorBuilder.buildFor(currentConfiguration.getEndpoint());
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
      if (apiConnector == null) {
        LOGGER.d("Endpoint is not well defined");
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

      List<EventModel> eventsToSent = eventsRepository.selectFirst(BATCH_MAX_SIZE);
      Collection<String> eventsToSentIds = extractEventsIds(eventsToSent);

      boolean dedupe = wasAtLeastOneTriedToSent(eventsToSent);
      List<EventRestModel> eventRestModels = translateEvents(eventsToSent);
      LOGGER.d("SendEventTask: Sending events: " + eventRestModels.size() + ", dedupe=" + dedupe);
      boolean success = apiConnector.sendEvents(eventRestModels, dedupe);
      LOGGER.d("SendEventTask: Events sent. Is success: " + success);
      if (success) {
        eventsRepository.delete(eventsToSentIds);
      } else {
        eventsRepository.updateSetWasTriedToSend(eventsToSentIds);
        // TODO
        LOGGER.e("SendEventTask: Sending events failed");
      }
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
    EventModel firstEvent = eventsRepository.selectFirst();
    if (firstEvent == null) {
      return null;
    }
    int queueSize = eventsRepository.countEvents();
    if (queueSize >= BATCH_MAX_SIZE) {
      return 0L;
    }
    long firstEventTime = firstEvent.getCreationTimestamp();
    long now = System.currentTimeMillis();
    long nextSendEventsTime = firstEventTime + BATCH_INTERVAL_MS;
    return (nextSendEventsTime > now) ? nextSendEventsTime - now : 0L;
  }

  private static Collection<String> extractEventsIds(Collection<EventModel> events) {
    HashSet<String> ids = new HashSet<>(events.size());
    for (EventModel event : events) {
      ids.add(event.getId());
    }
    return ids;
  }

  private static boolean wasAtLeastOneTriedToSent(List<EventModel> events) {
    for (EventModel event : events) {
      if (event.isWasTriedToSend()) {
        return true;
      }
    }
    return false;
  }

  private List<EventRestModel> translateEvents(List<EventModel> events) {
    List<EventRestModel> eventRestModels = new ArrayList<>(events.size());
    for (EventModel event : events) {
      EventRestModel eventRestModel = eventRestModelCreator.create(event);
      if (eventRestModel != null) {
        eventRestModels.add(eventRestModel);
      }
    }
    return eventRestModels;
  }

}
