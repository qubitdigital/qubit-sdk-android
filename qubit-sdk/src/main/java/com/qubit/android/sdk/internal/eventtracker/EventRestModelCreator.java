package com.qubit.android.sdk.internal.eventtracker;

import android.os.Build;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.qubit.android.sdk.BuildConfig;
import com.qubit.android.sdk.internal.eventtracker.connector.EventContext;
import com.qubit.android.sdk.internal.eventtracker.connector.EventMeta;
import com.qubit.android.sdk.internal.eventtracker.connector.EventRestModel;
import com.qubit.android.sdk.internal.eventtracker.connector.LifetimeValue;
import com.qubit.android.sdk.internal.eventtracker.repository.EventModel;
import com.qubit.android.sdk.internal.common.logging.QBLogger;
import com.qubit.android.sdk.internal.lookup.LookupData;

import org.jetbrains.annotations.NotNull;

class EventRestModelCreator {

  private static final QBLogger LOGGER = QBLogger.getFor("EventRestModelCreator");
  private static final int MAX_SAMPLE = 100000;

  private final JsonParser jsonParser = new JsonParser();
  private final String trackingId;
  private final String deviceId;
  private final int sample;
  private final String source = "Android@OS:" + Build.VERSION.SDK_INT + "@SDK:" + BuildConfig.VERSION_NAME;

  EventRestModelCreator(String trackingId, String deviceId) {
    this.trackingId = trackingId;
    this.deviceId = deviceId;
    this.sample = evaluateSample(deviceId);
  }

  public BatchEventRestModelCreator forBatch(@NotNull Long batchTimestamp, Integer timezoneOffsetMins,
                                             EventTypeTransformer eventTypeTransformer,
                                             LookupData lookupData) {
    return new BatchEventRestModelCreator(batchTimestamp, timezoneOffsetMins, eventTypeTransformer, lookupData);
  }

  public final class BatchEventRestModelCreator {
    @NotNull private final Long batchTimestamp;
    private final Integer timezoneOffsetMins;
    private final EventTypeTransformer eventTypeTransformer;
    private final LookupData lookupData;

    private BatchEventRestModelCreator(@NotNull Long batchTimestamp, Integer timezoneOffsetMins,
                                       EventTypeTransformer eventTypeTransformer, LookupData lookupData) {
      this.batchTimestamp = batchTimestamp;
      this.timezoneOffsetMins = timezoneOffsetMins;
      this.eventTypeTransformer = eventTypeTransformer;
      this.lookupData = lookupData;
    }

    public EventRestModel create(EventModel eventModel) {

      JsonObject event = parseJsonEvent(eventModel.getEventBody());
      if (event == null) {
        return null;
      }

      EventContext context = new EventContext(deviceId, sample);
      context.setSessionData(eventModel.getContextSessionNumber(), eventModel.getContextSessionTimestamp(),
          eventModel.getContextSessionViewNumber(), eventModel.getContextViewNumber(),
          eventModel.getContextViewTimestamp());
      context.setTimezoneOffset(timezoneOffsetMins);

      if (lookupData != null) {
        if (lookupData.getLifetimeValue() != null) {
          context.setLifetimeValue(new LifetimeValue(lookupData.getLifetimeValue(), "USD"));
        }
        context.setConversionNumber(lookupData.getConversionNumber());
        context.setConversionCycleNumber(lookupData.getConversionCycleNumber());
      }

      String eventType = eventTypeTransformer.transform(eventModel.getType());
      EventMeta meta = new EventMeta(eventModel.getGlobalId(), eventModel.getCreationTimestamp(), eventType,
          trackingId, eventModel.getSeq(), source, batchTimestamp);

      return new EventRestModel(event, meta, context);
    }

    private JsonObject parseJsonEvent(String jsonEvent) {
      try {
        return jsonParser.parse(jsonEvent).getAsJsonObject();
      } catch (JsonSyntaxException | NullPointerException | IllegalStateException e) {
        LOGGER.w("Event body stored in database cannot be parsed from JSON. Event: "
            + jsonEvent + ". Omitting event.", e);
        return null;
      }
    }

  }

  private static int evaluateSample(String deviceId) {
    int mod = deviceId.hashCode() % MAX_SAMPLE;
    return mod >= 0 ? mod : mod + MAX_SAMPLE;
  }

}
