package com.qubit.android.sdk.internal.eventtracker.repository;

import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.util.DateTimeUtils;
import com.qubit.android.sdk.internal.util.ListUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CachingEventsRepository implements EventsRepository {

  private static final QBLogger LOGGER = QBLogger.getFor("CachingEventsRepository");
  private static final long QUEUE_SIZE_UPDATE_INTERVAL_MS = DateTimeUtils.minToMs(5);

  private final EventsRepository eventsRepository;

  // cached values
  private Integer queueSize;
  private List<EventModel> queueBeginningCache;
  private Long queueSizeUpdateTime;

  public CachingEventsRepository(EventsRepository eventsRepository) {
    this.eventsRepository = eventsRepository;
  }

  @Override
  public EventModel insert(String type, String jsonEvent) {
    int sizeBefore = countEvents();
    EventModel newEvent = eventsRepository.insert(type, jsonEvent);
    if (queueBeginningCache != null && sizeBefore == queueBeginningCache.size()) {
      queueBeginningCache.add(newEvent);
    }
    queueSize++;
    return newEvent;
  }

  @Override
  public EventModel selectFirst() {
    if (queueBeginningCache != null) {
      return queueBeginningCache.isEmpty() ? null : queueBeginningCache.get(0);
    }
    EventModel firstEvent = eventsRepository.selectFirst();
    if (firstEvent == null && (queueSize == null || queueSize > 0)) {
      if (queueSize != null) {
        LOGGER.w("INCONSISTENCY! Query for first element returned nothing, while current size cache is: "
            + queueSize + ". Fixing size cache.");
      }
      queueSize = 0;
    }
    if (firstEvent != null && queueSize != null && queueSize == 0) {
      LOGGER.w("INCONSISTENCY! Query for first element returned element, while current size cache is 0. "
          + "Clearing queue size cache.");
      queueSize = null;
    }
    if (queueBeginningCache == null) {
      queueBeginningCache = new ArrayList<>();
    }
    if (firstEvent != null) {
      if (queueBeginningCache.isEmpty()) {
        queueBeginningCache.add(firstEvent);
      } else {
        queueBeginningCache.set(0, firstEvent);
      }
    }
    return firstEvent;
  }

  @Override
  public List<EventModel> selectFirst(int amount) {
    List<EventModel> eventsPlusOne = eventsRepository.selectFirst(amount + 1);

    // Check consistency with size cache
    if (eventsPlusOne.size() < amount + 1 && (queueSize == null || queueSize != eventsPlusOne.size())) {
      if (queueSize != null) {
        LOGGER.w(getInconsistencyMessage(amount + 1, eventsPlusOne.size(), queueSize) + ". Fixing.");
      }
      queueSize = eventsPlusOne.size();
    }
    if (queueSize != null && queueSize < eventsPlusOne.size()) {
      LOGGER.w(getInconsistencyMessage(amount + 1, eventsPlusOne.size(), queueSize) + ". Clearing queue size cache.");
      queueSize = null;
    }

    queueBeginningCache = eventsPlusOne;

    return ListUtil.firstElements(eventsPlusOne, amount);
  }


  @Override
  public boolean delete(String id) {
    boolean removed = eventsRepository.delete(id);
    deleteFromCache(Collections.singletonList(id));
    if (removed) {
      queueSize--;
    }
    return removed;
  }

  @Override
  public int delete(Collection<String> ids) {
    int removed = eventsRepository.delete(ids);
    deleteFromCache(ids);
    queueSize -= removed;
    return removed;
  }

  @Override
  public boolean updateSetWasTriedToSend(String id) {
    updateWasTriedToSendInCache(Collections.singletonList(id));
    return eventsRepository.updateSetWasTriedToSend(id);
  }

  @Override
  public int updateSetWasTriedToSend(Collection<String> ids) {
    updateWasTriedToSendInCache(ids);
    return eventsRepository.updateSetWasTriedToSend(ids);
  }

  @Override
  public int countEvents() {
    if (queueSize == null || queueSizeUpdateTime == null
        || queueSizeUpdateTime + QUEUE_SIZE_UPDATE_INTERVAL_MS < System.currentTimeMillis()) {
      queueSize = eventsRepository.countEvents();
      queueSizeUpdateTime = System.currentTimeMillis();
    }
    return queueSize;
  }


  private int deleteFromCache(Collection<String> ids) {
    if (queueBeginningCache == null) {
      return 0;
    }
    Iterator<EventModel> eventsIterator = queueBeginningCache.iterator();
    int eventsSizeBefore = queueBeginningCache.size();
    while (eventsIterator.hasNext()) {
      EventModel event = eventsIterator.next();
      if (ids.contains(event.getId())) {
        eventsIterator.remove();
      }
    }
    return eventsSizeBefore - queueBeginningCache.size();
  }

  private int updateWasTriedToSendInCache(Collection<String> ids) {
    if (queueBeginningCache == null) {
      return 0;
    }
    int updatesCounter = 0;
    for (EventModel event: queueBeginningCache) {
      if (ids.contains(event.getId())) {
        event.setWasTriedToSend(true);
        updatesCounter++;
      }
    }
    return updatesCounter;
  }

  private static String getInconsistencyMessage(int queriedFor, int returned, int currentQueueSize) {
    return "INCONSISTENCY! Query for " + queriedFor + " elements returned " + returned
        + "elements, while current size cache is: " + currentQueueSize;
  }

}
