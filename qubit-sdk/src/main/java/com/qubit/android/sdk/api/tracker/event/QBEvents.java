package com.qubit.android.sdk.api.tracker.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.qubit.android.sdk.internal.common.model.QBEventImpl;
import java.util.Map;

/**
 * Collection of methods for building events.
 */
public final class QBEvents {

  private static final Gson GSON = new Gson();

  public static class JsonParseException extends RuntimeException {
    public JsonParseException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private QBEvents() {
  }

  /**
   * Builds an event from string containing JSON object.
   * @param type Type of event e.g. "ecView". It cannot be null neither empty string.
   * @param jsonString String containing JSON object e.g. "{ \"type\" : \"home\" }. It cannot be null.
   * @return QBEvent object which can be used in
   * {@link com.qubit.android.sdk.api.tracker.EventTracker#sendEvent(QBEvent)}
   * @throws JsonParseException string is not well formatted JSON or it doesn't contain JSON object.
   * @throws NullPointerException At least one of parameters is null.
   * @throws IllegalArgumentException type is null
   */
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

  /**
   * Builds an event from GSON's JsonObject.
   * @param type Type of event e.g. "ecView". It cannot be null neither empty string.
   * @param jsonObject GSON's JsonObject.  It cannot be null.
   * @return QBEvent object which can be used in
   *     {@link com.qubit.android.sdk.api.tracker.EventTracker#sendEvent(QBEvent)}
   * @throws NullPointerException At least one of parameters is null.
   * @throws IllegalArgumentException type is null
   */
  public static QBEvent fromJson(final String type, final JsonObject jsonObject) {
    validateType(type);
    validateEventBody("jsonObject", jsonObject);
    return new QBEventImpl(type, jsonObject);
  }

  /**
   * Builds an event from Object.
   * Object is converted using GSON library (https://github.com/google/gson)
   *
   * @param type Type of event e.g. "ecView". It cannot be null neither empty string.
   * @param object Any object. It cannot be null.
   * @return QBEvent object which can be used in
   *     {@link com.qubit.android.sdk.api.tracker.EventTracker#sendEvent(QBEvent)}
   * @throws NullPointerException At least one of parameters is null.
   * @throws IllegalArgumentException type is null
   */
  public static QBEvent fromObject(final String type, final Object object) {
    validateType(type);
    if (object == null) {
      throw new NullPointerException("Object parameter cannot be null");
    }
    return new QBEventImpl(type, GSON.toJsonTree(object).getAsJsonObject());
  }


  /**
   * Builds an event from Map.
   * Map is converted using GSON library (https://github.com/google/gson)
   *
   * @param type Type of event e.g. "ecView". It cannot be null neither empty string.
   * @param map Map containing properties of event.  It cannot be null.
   * @return QBEvent object which can be used in
   *     {@link com.qubit.android.sdk.api.tracker.EventTracker#sendEvent(QBEvent)}
   * @throws NullPointerException At least one of parameters is null.
   * @throws IllegalArgumentException type is null
   */
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
    if (type.isEmpty()) {
      throw new IllegalArgumentException("Type parameter cannot be empty");
    }
  }


}
