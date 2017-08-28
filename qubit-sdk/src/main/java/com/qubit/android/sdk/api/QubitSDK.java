package com.qubit.android.sdk.api;

import com.qubit.android.sdk.api.initialization.InitializationBuilder;
import com.qubit.android.sdk.api.tracker.QBTracker;
import com.qubit.android.sdk.api.tracker.event.QBEvent;

public abstract class QubitSDK {

  public static InitializationBuilder initialization() {
    return new InitializationBuilder();
  }

  public static QBTracker tracker() {
    // TODO
    return new QBTracker() {
      @Override
      public void sendEvent(String type, QBEvent event) {

      }

      @Override
      public void enable() {

      }

      @Override
      public void enable(boolean enable) {

      }

      @Override
      public void disable() {

      }
    };
  }

}
