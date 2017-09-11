package com.qubit.android.sdk.api.tracker.event;

import com.google.gson.JsonObject;

public interface QBEvent {

  String getType();
  JsonObject toJsonObject();

}
