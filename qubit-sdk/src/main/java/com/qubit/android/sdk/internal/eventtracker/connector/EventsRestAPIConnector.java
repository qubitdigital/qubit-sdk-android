package com.qubit.android.sdk.internal.eventtracker.connector;

import java.util.List;

public interface EventsRestAPIConnector {
  boolean sendEvents(List<EventRestModel> events, boolean dedupe);
}
