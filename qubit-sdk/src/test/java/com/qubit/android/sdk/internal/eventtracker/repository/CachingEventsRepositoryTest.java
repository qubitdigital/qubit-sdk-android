package com.qubit.android.sdk.internal.eventtracker.repository;

import java.util.ArrayList;
import java.util.List;
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

    cachingEventsRepository.selectFirst(5);

    cachingEventsRepository.delete(storedEventModelsIds.subList(0, 6));

    cachingEventsRepository.insert(createTestEventModel());
    assertNotNull(cachingEventsRepository.selectFirst());
  }

  private static EventModel createTestEventModel() {
    return new EventModel(null, "globalId", 1, "view", "{}", false, System.currentTimeMillis());
  }

}
