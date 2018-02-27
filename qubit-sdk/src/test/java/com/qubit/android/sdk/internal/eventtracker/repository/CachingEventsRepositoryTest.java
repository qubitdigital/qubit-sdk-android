package com.qubit.android.sdk.internal.eventtracker.repository;

import android.app.usage.UsageEvents;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Test;

import static org.junit.Assert.*;

public class CachingEventsRepositoryTest {
  @Test
  public void selectFirst() throws Exception {

    EventsRepositoryMock eventsRepositoryMock = new EventsRepositoryMock();
    CachingEventsRepository cachingEventsRepository = new CachingEventsRepository(eventsRepositoryMock);
    cachingEventsRepository.init();

    ArrayList<Long> storedEventModelsIds = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      storedEventModelsIds.add(cachingEventsRepository.insert(createTestEventModel()).getId());
    }

    List<EventModel> first5Events = cachingEventsRepository.selectFirst(5);

    cachingEventsRepository.delete(storedEventModelsIds.subList(0,6));

    cachingEventsRepository.insert(createTestEventModel());
    assertNotNull(cachingEventsRepository.selectFirst());
  }

  private static EventModel createTestEventModel() {
    return new EventModel(null, "globalId", 1, "view", "{}", false, System.currentTimeMillis());
  }

}