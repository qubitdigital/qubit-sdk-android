package com.qubit.android.sdk.internal.eventtracker.repository;

import java.util.Collection;
import java.util.List;

public interface EventsRepository {

  long insert(String type, String jsonEvent);

  List<EventModel> selectFirst(int number);

  boolean delete(long id);

  int delete(Collection<Long> ids);

  boolean updateSetWasTriedToSend(long id);

  int updateSetWasTriedToSend(Collection<Long> ids);

  int countEvents();
}
