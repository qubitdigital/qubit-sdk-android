package com.qubit.android.sdk.api;

import com.qubit.android.sdk.api.initialization.InitializationBuilder;
import com.qubit.android.sdk.api.tracker.QBTracker;
import com.qubit.android.sdk.api.tracker.event.QBEvent;

public abstract class QubitSDK {

  public static InitializationBuilder initialization() {
    return new InitializationBuilder();
  }

  public static QBTracker tracker() {
    return new QBTracker() {
      @Override
      public void sendEvent(String type, QBEvent event) {

      }
    }; // TODO
  }

}
