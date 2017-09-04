package com.qubit.android.sdk.internal.eventtracker.connector;

import java.io.IOException;
import java.util.List;
import retrofit2.Response;

public class EventsRestAPIConnectorImpl implements EventsRestAPIConnector {

  private static final EventRestModel[] EMPTY_EVENT_LIST = new EventRestModel[0];

  private final String trackingId;
  private final EventsRestAPI api;

  public EventsRestAPIConnectorImpl(String trackingId, EventsRestAPI api) {
    this.trackingId = trackingId;
    this.api = api;
  }

  @Override
  public boolean sendEvents(List<EventRestModel> events, boolean dedupe) {
    if (api == null) {
      throw new IllegalStateException("EventSender: Before sending events, endpoint url has to be set. "
          + "Use setEndpointUrl method");
    }
    // TODO
    try {
      Response<RestApiResponse> response =
          api.sendEvents(trackingId, dedupe, events.toArray(EMPTY_EVENT_LIST)).execute();
      // TODO exception handling
      return true;
    } catch (IOException e) {
      // TODO exception handling
      e.printStackTrace();
      return false;
    }
  }


}
