package com.qubit.android.sdk.internal.eventtracker.repository;

import com.qubit.android.sdk.internal.common.logging.QBLogger;
import com.qubit.android.sdk.internal.session.SessionService;
import com.qubit.android.sdk.internal.common.util.DateTimeUtils;
import com.qubit.android.sdk.internal.common.util.ListUtil;
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
  /**
   * Cached beginning of queue.
   * Value meaning:
   * Null - no knowledge about state of queue.
   * Empty - queue is empty.
   * N elements - queue contains these N elements at the beginning.
   *              It means only that queue contains at least N elements, but it can be longer.
   */
  private List<EventModel> queueBeginningCache;
  private Long queueSizeUpdateTime;

  public CachingEventsRepository(EventsRepository eventsRepository) {
    this.eventsRepository = eventsRepository;
  }

  @Override
  public boolean init() {
    return eventsRepository.init();
  }

  @Override
  public EventModel insert(EventModel newEvent) {
    int sizeBefore = count();
    EventModel insertedNewEvent = eventsRepository.insert(newEvent);
    if (queueBeginningCache != null && sizeBefore == queueBeginningCache.size()) {
      queueBeginningCache.add(insertedNewEvent);
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
  public boolean delete(long id) {
    boolean removed = eventsRepository.delete(id);
    if (removed) {
      queueSize--;
    }
    deleteFromCacheByIds(Collections.singletonList(id));
    return removed;
  }

  @Override
  public int delete(Collection<Long> ids) {
    int removed = eventsRepository.delete(ids);
    queueSize -= removed;
    deleteFromCacheByIds(ids);
    return removed;
  }

  @Override
  public int deleteOlderThan(long timeMs) {
    int removed = eventsRepository.deleteOlderThan(timeMs);
    queueSize -= removed;
    int removedFromCache = deleteFromCacheOlderThan(timeMs);
    LOGGER.d("Old events removed from cache: " + removedFromCache);
    return removed;
  }

  @Override
  public boolean updateSetWasTriedToSend(long id) {
    updateWasTriedToSendInCache(Collections.singletonList(id));
    return eventsRepository.updateSetWasTriedToSend(id);
  }

  @Override
  public int updateSetWasTriedToSend(Collection<Long> ids) {
    updateWasTriedToSendInCache(ids);
    return eventsRepository.updateSetWasTriedToSend(ids);
  }

  @Override
  public int count() {
    if (queueSize == null || queueSizeUpdateTime == null
        || queueSizeUpdateTime + QUEUE_SIZE_UPDATE_INTERVAL_MS < System.currentTimeMillis()) {
      queueSize = eventsRepository.count();
      queueSizeUpdateTime = System.currentTimeMillis();
    }
    return queueSize;
  }

  private interface Predicate<T> {
    boolean test(T t);
  }

  private int deleteFromCacheByIds(final Collection<Long> ids) {
    return deleteFromCache(new Predicate<EventModel>() {
      @Override
      public boolean test(EventModel event) {
        return ids.contains(event.getId());
      }
    });
  }

  private int deleteFromCacheOlderThan(final long timeMs) {
    return deleteFromCache(new Predicate<EventModel>() {
      @Override
      public boolean test(EventModel event) {
        return event.getCreationTimestamp() < timeMs && !event.getType().equals(SessionService.SESSION_EVENT_TYPE);
      }
    });
  }

  private int deleteFromCache(Predicate<EventModel> predicate) {
    if (queueBeginningCache == null) {
      return 0;
    }
    Iterator<EventModel> eventsIterator = queueBeginningCache.iterator();
    int eventsSizeBefore = queueBeginningCache.size();
    while (eventsIterator.hasNext()) {
      EventModel event = eventsIterator.next();
      if (predicate.test(event)) {
        eventsIterator.remove();
      }
    }
    int removed = eventsSizeBefore - queueBeginningCache.size();
    if (queueBeginningCache.size() == 0 && queueSize > 0) {
      // removing all elements in cache doesn't mean that whole queue is empty
      queueBeginningCache = null;
    }
    return removed;
  }

  private int updateWasTriedToSendInCache(Collection<Long> ids) {
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
