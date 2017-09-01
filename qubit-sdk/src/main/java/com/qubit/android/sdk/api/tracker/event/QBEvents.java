package com.qubit.android.sdk.api.tracker.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.qubit.android.sdk.internal.logging.QBLogger;
import java.util.Arrays;
import java.util.Map;

public abstract class QBEvents {

  private static final QBLogger LOGGER = QBLogger.getFor("QBEvents");

  public static QBEvent fromJsonString(final String jsonString) {
    return new QBEvent() {
      @Override
      public JsonObject toJsonObject() {
        try {
          JsonParser jsonParser = new JsonParser();
          return jsonParser.parse(jsonString).getAsJsonObject();
        } catch (JsonSyntaxException | NullPointerException | IllegalStateException e) {
          LOGGER.e("Failed to parse following string to JSON object: " + jsonString, e);
          return null;
        }
      }
    };
  }

  public static QBEvent fromJson(final JsonObject jsonObject) {
    return new QBEvent() {
      @Override
      public JsonObject toJsonObject() {
        return jsonObject;
      }
    };
  }

  public static QBEvent fromBean(final Object bean) {
    return new QBEvent() {
      @Override
      public JsonObject toJsonObject() {
        try {
          // TODO extract Gson
          Gson gson = new Gson();
          return gson.toJsonTree(bean).getAsJsonObject();
        } catch (IllegalStateException e) {
          LOGGER.e("Failed to parse bean to JSON object", e);
          return null;
        }
      }
    };
  }

  public static QBEvent fromMap(final Map<String, Object> map) {
    return new QBEvent() {
      @Override
      public JsonObject toJsonObject() {
        try {
          // TODO extract Gson
          Gson gson = new Gson();
          return gson.toJsonTree(map).getAsJsonObject();
        } catch (IllegalStateException e) {
          String mapAsString = map != null ? Arrays.toString(map.entrySet().toArray()) : "null";
          LOGGER.e("Failed to parse following map to JSON object: " + mapAsString, e);
          return null;
        }
      }
    };
  }

}
