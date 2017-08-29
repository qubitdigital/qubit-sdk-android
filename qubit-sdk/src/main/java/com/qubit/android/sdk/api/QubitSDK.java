package com.qubit.android.sdk.api;

import com.qubit.android.sdk.api.initialization.InitializationBuilder;
import com.qubit.android.sdk.api.tracker.EventTracker;
import com.qubit.android.sdk.api.tracker.event.QBEvent;

public abstract class QubitSDK {

  public static InitializationBuilder initialization() {
    return new InitializationBuilder();
  }

  public static EventTracker tracker() {
    // TODO
    return new EventTracker() {
      @Override
      public void sendEvent(String type, QBEvent event) {

      }

      @Override
      public void enable(boolean enable) {

      }
    };
  }

}
