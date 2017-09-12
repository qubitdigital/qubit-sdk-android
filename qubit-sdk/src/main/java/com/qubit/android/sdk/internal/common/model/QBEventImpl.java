package com.qubit.android.sdk.internal.common.model;

import com.google.gson.JsonObject;
import com.qubit.android.sdk.api.tracker.event.QBEvent;

public final class QBEventImpl implements QBEvent {
  private final String type;
  private final JsonObject jsonObject;

  public QBEventImpl(String type, JsonObject jsonObject) {
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
