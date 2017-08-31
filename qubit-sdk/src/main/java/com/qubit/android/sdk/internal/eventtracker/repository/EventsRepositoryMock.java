package com.qubit.android.sdk.internal.eventtracker.repository;

import com.qubit.android.sdk.internal.util.ListUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EventsRepositoryMock implements EventsRepository {

  private static long idSequence = 1;
  private final List<EventModel> events = new ArrayList<>();


  @Override
  public long insert(String type, String jsonEvent) {
    long id = idSequence++;
    EventModel newEvent = new EventModel(id, type, jsonEvent, System.currentTimeMillis());
    events.add(newEvent);
    return id;
  }

  @Override
  public List<EventModel> selectFirst(int number) {
    return new ArrayList<>(ListUtil.firstElements(events, number));
  }

  @Override
  public boolean delete(long id) {
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
  public boolean updateSetWasTriedToSend(long id) {
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
  public int countEvents() {
    return events.size();
  }
}
