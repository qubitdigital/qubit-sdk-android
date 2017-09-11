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

  public static QBEvent fromJsonString(final String type, final String jsonString) throws JsonParseException {
    if (type == null) {
      throw new NullPointerException("type parameter cannot be null");
    }
    if (jsonString == null) {
      throw new NullPointerException("jsonString parameter cannot be null");
    }
    try {
      JsonParser jsonParser = new JsonParser();
      JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
      return new QBEventImpl(type, jsonObject);
    } catch (JsonSyntaxException | IllegalStateException e) {
      throw new JsonParseException("Failed to parse following string to JSON object: " + jsonString, e);
    }
  }

  public static QBEvent fromJson(final String type, final JsonObject jsonObject) {
    if (type == null) {
      throw new NullPointerException("type parameter cannot be null");
    }
    if (jsonObject == null) {
      throw new NullPointerException("jsonObject parameter cannot be null");
    }
    return new QBEventImpl(type, jsonObject);
  }


  public static QBEvent fromObject(final String type, final Object object) {
    if (type == null) {
      throw new NullPointerException("type parameter cannot be null");
    }
    if (object == null) {
      throw new NullPointerException("Object parameter cannot be null");
    }
    return new QBEventImpl(type, GSON.toJsonTree(object).getAsJsonObject());
  }

  public static QBEvent fromMap(final String type, final Map<String, Object> map) {
    if (type == null) {
      throw new NullPointerException("type parameter cannot be null");
    }
    if (map == null) {
      throw new NullPointerException("Map parameter cannot be null");
    }
    return new QBEventImpl(type, GSON.toJsonTree(map).getAsJsonObject());
  }


  private static final class QBEventImpl implements QBEvent {
    private final String type;
    private final JsonObject jsonObject;

    private QBEventImpl(String type, JsonObject jsonObject) {
      this.type = type;
      this.jsonObject = jsonObject;
    }

    @Override
    public String getType() {
      return type;
    }

    @Override
    public JsonObject toJsonObject() {
      return jsonObject;
    }
  }

}
