package com.qubit.android.sdk.api;

import com.qubit.android.sdk.api.tracker.EventTracker;
import com.qubit.android.sdk.internal.SDK;

/**
 * Main entry point of Qubit SDK.
 */
public final class QubitSDK {

  private static SDK sdkSingleton;

  private QubitSDK() {
  }

  /**
   * Fluent interface for initialization of SDK, allowing to set configuration parameters and start SDK.
   * @return Fluent interface for setting configuration parameters and starting SDK.
   */
  public static InitializationBuilder initialization() {
    if (sdkSingleton != null) {
      throw new IllegalStateException("QubitSDK has been already started.");
    }
    return new InitializationBuilder(new InitializationBuilder.SdkConsumer() {
      @Override
      public void accept(SDK sdk) {
        sdkSingleton = sdk;
      }
    });
  }

  /**
   * Event tracker interface for sending events.
   * @return Event tracker
   */
  public static EventTracker tracker() {
    checkSdkInitialized();
    return sdkSingleton.getEventTracker();
  }

  /**
   * Release all resources used by SDK, including stopping all background threads.
   */
  public static void release() {
    checkSdkInitialized();
    sdkSingleton.stop();
    sdkSingleton = null;
  }

  public static String getDeviceId() {
    checkSdkInitialized();
    return sdkSingleton.getDeviceId();
  }

  public static String getTrackingId() {
    checkSdkInitialized();
    return sdkSingleton.getTrackingId();
  }

  private static void checkSdkInitialized() {
    if (sdkSingleton == null) {
      throw new IllegalStateException("QubitSDK is not initialized yet. "
          + "Call QubitSDK.initialization().{...}.start() before any other call to QubitSDK");
    }
  }

}
