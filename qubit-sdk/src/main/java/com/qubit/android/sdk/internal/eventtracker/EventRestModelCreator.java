package com.qubit.android.sdk.internal.eventtracker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.qubit.android.sdk.internal.eventtracker.connector.EventContext;
import com.qubit.android.sdk.internal.eventtracker.connector.EventMeta;
import com.qubit.android.sdk.internal.eventtracker.connector.EventRestModel;
import com.qubit.android.sdk.internal.eventtracker.repository.EventModel;
import com.qubit.android.sdk.internal.logging.QBLogger;

class EventRestModelCreator {

  private static final QBLogger LOGGER = QBLogger.getFor("EventRestModelCreator");
  private static final int MAX_SAMPLE = 100000;

  private final JsonParser jsonParser = new JsonParser();
  private final String trackingId;
  private final String deviceId;
  private final int sample;

  EventRestModelCreator(String trackingId, String deviceId) {
    this.trackingId = trackingId;
    this.deviceId = deviceId;
    this.sample = evaluateSample(deviceId);
  }

  public EventRestModel create(EventModel eventModel, Long batchTimestamp) {

    JsonObject event = parseJsonEvent(eventModel.getEventBody());
    if (event == null) {
      return null;
    }

    EventContext context = new EventContext(deviceId, sample);
    context.setSessionData(eventModel.getContextSessionNumber(), eventModel.getContextSessionTimestamp(),
        eventModel.getContextSessionViewNumber(), eventModel.getContextViewNumber(),
        eventModel.getContextViewTimestamp());
    EventMeta meta = new EventMeta(eventModel.getGlobalId(), eventModel.getCreationTimestamp(), eventModel.getType(),
        trackingId, eventModel.getSeq(), null, batchTimestamp);

    return new EventRestModel(event, meta, context);
  }

  private JsonObject parseJsonEvent(String jsonEvent) {
    try {
      return jsonParser.parse(jsonEvent).getAsJsonObject();
    } catch (JsonSyntaxException | NullPointerException | IllegalStateException e) {
      LOGGER.w("Event body stored in database cannot be parsed from JSON. Event: "
          + jsonEvent + ". Omiting event.", e);
      return null;
    }
  }

  private static int evaluateSample(String deviceId) {
    int mod = deviceId.hashCode() % MAX_SAMPLE;
    return mod >= 0 ? mod : mod + MAX_SAMPLE;
  }


}
