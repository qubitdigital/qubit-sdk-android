package com.qubit.android.sdk.internal.eventtracker.repository;

import java.util.Collection;
import java.util.List;

public interface EventsRepository {

  EventModel insert(String type, String jsonEvent);

  EventModel selectFirst();

  List<EventModel> selectFirst(int number);

  boolean delete(String id);

  int delete(Collection<String> ids);

  boolean updateSetWasTriedToSend(String id);

  int updateSetWasTriedToSend(Collection<String> ids);

  int countEvents();
}
