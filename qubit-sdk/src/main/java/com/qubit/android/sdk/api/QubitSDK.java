package com.qubit.android.sdk.api;

import com.qubit.android.sdk.api.placement.Placement;
import com.qubit.android.sdk.api.placement.PlacementMode;
import com.qubit.android.sdk.api.placement.PlacementPreviewOptions;
import com.qubit.android.sdk.api.tracker.EventTracker;
import com.qubit.android.sdk.internal.SDK;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Main entry point of Qubit SDK.
 */
public final class QubitSDK {

  private static SDK sdkSingleton;

  private QubitSDK() {
  }

  /**
   * Fluent interface for initialization of SDK, allowing to set configuration parameters and start SDK.
   *
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
   *
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

  /**
   * Fetch placement defined for given criteria.
   * Method is asynchronous, result is returned through passed callback functions.
   *
   * @param placementId    The unique ID of the placement.
   * @param mode           The mode to fetch placements content with. Defaults to [PlacementMode.LIVE]
   * @param previewOptions Additional criteria used to query for the placement.
   * @param onSuccess      Callback invoked when query succeeds. Contains [Placement] object or null if no placement meets given criteria.
   * @param onError        Callback invoked when query fails.
   */
  public static void getPlacement(
      @NotNull String placementId,
      @Nullable PlacementMode mode,
      @NotNull PlacementPreviewOptions previewOptions,
      @NotNull Function1<? super Placement, Unit> onSuccess,
      @NotNull Function1<? super Throwable, Unit> onError
  ) {
    sdkSingleton.getPlacementInteractor()
        .fetchPlacement(placementId, mode, previewOptions, onSuccess, onError);
  }

}
