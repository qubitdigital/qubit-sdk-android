package com.qubit.android.sdk.internal.eventtracker.connector;

import java.util.List;

public interface EventsRestAPIConnector {

  enum ResponseStatus {
    OK, RETRYABLE_ERROR, ERROR
  }

  ResponseStatus sendEvents(List<EventRestModel> events, boolean dedupe);
}
