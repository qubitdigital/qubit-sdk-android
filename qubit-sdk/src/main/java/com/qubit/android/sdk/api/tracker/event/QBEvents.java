package com.qubit.android.sdk.api.tracker.event;

import java.util.Map;
import org.json.JSONObject;

public abstract class QBEvents {

  public static QBEvent fromJsonString(String jsonString) {
    // TODO
    return new QBEvent() {
      @Override
      public JSONObject toJsonObject() {
        return new JSONObject();
      }
    };
  }

  public static QBEvent fromJson(JSONObject jsonObject) {
    // TODO
    return null;
  }

  public static QBEvent fromBean(Object bean) {
    // TODO
    return null;
  }

  public static QBEvent fromMap(Map<String, Object> map) {
    // TODO
    return null;
  }

}
