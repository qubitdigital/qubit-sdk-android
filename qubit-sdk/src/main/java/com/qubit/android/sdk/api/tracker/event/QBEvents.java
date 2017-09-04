package com.qubit.android.sdk.api.tracker.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.Map;

public final class QBEvents {

  private static final Gson GSON = new Gson();

  public static class JsonParseException extends RuntimeException {
    public JsonParseException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private QBEvents() {
  }

  public static QBEvent fromJsonString(final String jsonString) throws JsonParseException {
    if (jsonString == null) {
      throw new NullPointerException("jsonString parameter cannot be null");
    }
    try {
      JsonParser jsonParser = new JsonParser();
      JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
      return new QBEventImpl(jsonObject);
    } catch (JsonSyntaxException | IllegalStateException e) {
      throw new JsonParseException("Failed to parse following string to JSON object: " + jsonString, e);
    }
  }

  public static QBEvent fromJson(final JsonObject jsonObject) {
    if (jsonObject == null) {
      throw new NullPointerException("jsonObject parameter cannot be null");
    }
    return new QBEventImpl(jsonObject);
  }


  public static QBEvent fromObject(final Object object) {
    if (object == null) {
      throw new NullPointerException("Object parameter cannot be null");
    }
    return new QBEventImpl(GSON.toJsonTree(object).getAsJsonObject());
  }

  public static QBEvent fromMap(final Map<String, Object> map) {
    if (map == null) {
      throw new NullPointerException("Map parameter cannot be null");
    }
    return new QBEventImpl(GSON.toJsonTree(map).getAsJsonObject());
  }


  private static final class QBEventImpl implements QBEvent {
    private final JsonObject jsonObject;

    private QBEventImpl(JsonObject jsonObject) {
      this.jsonObject = jsonObject;
    }

    @Override
    public JsonObject toJsonObject() {
      return jsonObject;
    }
  }

}
