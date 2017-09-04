package com.qubit.android.sdk.internal.eventtracker.connector;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class EventRestModel {
  private final JsonObject eventBody;
  private final EventMeta meta;
  private final EventContext context;

  public EventRestModel(JsonObject eventBody, EventMeta meta, EventContext context) {
    this.eventBody = eventBody;
    this.meta = meta;
    this.context = context;
  }

  public JsonObject getEventBody() {
    return eventBody;
  }

  public EventMeta getMeta() {
    return meta;
  }

  public EventContext getContext() {
    return context;
  }

  public static class Serializer implements JsonSerializer<EventRestModel> {
    @Override
    public JsonElement serialize(EventRestModel src, Type typeOfSrc,
                                 JsonSerializationContext jsonSerializationContext) {

      JsonElement context = jsonSerializationContext.serialize(src.getContext());
      JsonElement meta = jsonSerializationContext.serialize(src.getMeta());

      JsonObject event = new JsonObject();
      event.add("context", context);
      event.add("meta", meta);

      Iterator<Map.Entry<String, JsonElement>> it = src.getEventBody().entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<String, JsonElement> entry = it.next();
        event.add(entry.getKey(), entry.getValue());
      }

      return event;
    }
  }


}
