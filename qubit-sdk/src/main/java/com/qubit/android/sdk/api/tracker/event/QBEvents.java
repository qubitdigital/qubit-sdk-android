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
    validateType(type);
    validateEventBody("jsonString", jsonString);
    try {
      JsonParser jsonParser = new JsonParser();
      JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
      return new QBEventImpl(type, jsonObject);
    } catch (JsonSyntaxException | IllegalStateException e) {
      throw new JsonParseException("Failed to parse following string to JSON object: " + jsonString, e);
    }
  }

  public static QBEvent fromJson(final String type, final JsonObject jsonObject) {
    validateType(type);
    validateEventBody("jsonObject", jsonObject);
    return new QBEventImpl(type, jsonObject);
  }

  public static QBEvent fromObject(final String type, final Object object) {
    validateType(type);
    if (object == null) {
      throw new NullPointerException("Object parameter cannot be null");
    }
    return new QBEventImpl(type, GSON.toJsonTree(object).getAsJsonObject());
  }


  public static QBEvent fromMap(final String type, final Map<String, Object> map) {
    validateType(type);
    validateEventBody("map", map);
    return new QBEventImpl(type, GSON.toJsonTree(map).getAsJsonObject());
  }

  private static void validateEventBody(String parameterName, Object parameter) {
    if (parameter == null) {
      throw new NullPointerException(parameterName + " parameter cannot be null");
    }
  }

  private static void validateType(String type) {
    if (type == null) {
      throw new NullPointerException("type parameter cannot be null");
    }
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
