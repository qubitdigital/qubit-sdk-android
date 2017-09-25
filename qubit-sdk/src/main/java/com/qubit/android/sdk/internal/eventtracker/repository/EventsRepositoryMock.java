package com.qubit.android.sdk.internal.eventtracker.repository;

import com.qubit.android.sdk.internal.common.logging.QBLogger;
import com.qubit.android.sdk.internal.session.SessionService;
import com.qubit.android.sdk.internal.common.util.ListUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EventsRepositoryMock implements EventsRepository {

  private static final QBLogger LOGGER = QBLogger.getFor("EventsRepositoryMock");

  private static long idSequence = 1;
  private final List<EventModel> events = new ArrayList<>();

  @Override
  public boolean init() {
    return true;
  }

  @Override
  public EventModel insert(EventModel newEvent) {
    LOGGER.d("insert");
    newEvent.setId(idSequence++);
    events.add(new EventModel(newEvent));
    return newEvent;
  }

  @Override
  public EventModel selectFirst() {
    LOGGER.d("selectFirst");
    return events.isEmpty() ? null : events.get(0);
  }

  @Override
  public List<EventModel> selectFirst(int number) {
    LOGGER.d("selectFirstN");
    return new ArrayList<>(ListUtil.firstElements(events, number));
  }

  @Override
  public boolean delete(long id) {
    LOGGER.d("delete one");
    Iterator<EventModel> eventsIterator = events.iterator();
    while (eventsIterator.hasNext()) {
      EventModel event = eventsIterator.next();
      if (event.getId() == id) {
        eventsIterator.remove();
        return true;
      }
    }
    return false;
  }

  @Override
  public int delete(Collection<Long> ids) {
    LOGGER.d("delete many");
    Iterator<EventModel> eventsIterator = events.iterator();
    int eventsSizeBefore = events.size();
    while (eventsIterator.hasNext()) {
      EventModel event = eventsIterator.next();
      if (ids.contains(event.getId())) {
        eventsIterator.remove();
      }
    }
    return eventsSizeBefore - events.size();
  }

  @Override
  public int deleteOlderThan(long timeMs) {
    Iterator<EventModel> eventsIterator = events.iterator();
    int eventsSizeBefore = events.size();
    while (eventsIterator.hasNext()) {
      EventModel event = eventsIterator.next();
      if (event.getCreationTimestamp() < timeMs && !event.getType().equals(SessionService.SESSION_EVENT_TYPE)) {
        eventsIterator.remove();
      }
    }
    return eventsSizeBefore - events.size();
  }

  @Override
  public boolean updateSetWasTriedToSend(long id) {
    LOGGER.d("updateSetWasTriedToSend");
    for (EventModel event: events) {
      if (event.getId() == id) {
        event.setWasTriedToSend(true);
        return true;
      }
    }
    return false;
  }

  @Override
  public int updateSetWasTriedToSend(Collection<Long> ids) {
    LOGGER.d("updateSetWasTriedToSend many");
    int updatesCounter = 0;
    for (EventModel event: events) {
      if (ids.contains(event.getId())) {
        event.setWasTriedToSend(true);
        updatesCounter++;
      }
    }
    return updatesCounter;
  }

  @Override
  public int count() {
    LOGGER.d("count");
    return events.size();
  }
}
