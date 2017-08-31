package com.qubit.android.sdk.api;

import com.qubit.android.sdk.api.initialization.InitializationBuilder;
import com.qubit.android.sdk.api.tracker.EventTracker;
import com.qubit.android.sdk.internal.SDK;

public final class QubitSDK {

  private static SDK sdkSingleton;

  private QubitSDK() {
  }

  public static InitializationBuilder initialization() {
    return new InitializationBuilder(new InitializationBuilder.SdkConsumer() {
      @Override
      public void accept(SDK sdk) {
        sdkSingleton = sdk;
      }
    });
  }

  public static EventTracker tracker() {
    if (sdkSingleton == null) {
      throw new IllegalStateException("QubitSDK is not initialized yet. "
          + "Call QubitSDK.initialization().{...}.start() before any other call to QubitSDK");
    }
    return sdkSingleton.getEventTracker();
  }

}
