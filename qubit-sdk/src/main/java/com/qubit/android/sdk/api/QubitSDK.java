package com.qubit.android.sdk.api;

import android.content.Context;

import com.google.gson.JsonObject;
import com.qubit.android.sdk.api.placement.Placement;
import com.qubit.android.sdk.api.placement.PlacementMode;
import com.qubit.android.sdk.api.placement.PlacementPreviewOptions;
import com.qubit.android.sdk.api.tracker.EventTracker;
import com.qubit.android.sdk.internal.SDK;
import com.qubit.android.sdk.internal.experience.Experience;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

  public static void restartWithCustomDeviceId(@Nullable String deviceId) {
    if (sdkSingleton != null) {
      // SDK is already initialized so it has to be restarted with a new deviceId
      Context appContext = sdkSingleton.getAppContext();
      String trackingId = sdkSingleton.getTrackingId();

      release();
      initialization()
       .withCustomDeviceId(deviceId)
       .inAppContext(appContext)
       .withTrackingId(trackingId)
       .start();
    }
  }

  public static String getTrackingId() {
    checkSdkInitialized();
    return sdkSingleton.getTrackingId();
  }

  /**
   * Send callback request to given URL.
   * In case of offline state request will be enqueued and delivered once device turns online.
   *
   * @param callbackUrl callback URL
   */
  public static void sendCallbackRequest(String callbackUrl) {
    checkSdkInitialized();
    sdkSingleton.getCallbackRequestTracker().scheduleRequest(callbackUrl);
  }

  private static void checkSdkInitialized() {
    if (sdkSingleton == null) {
      throw new IllegalStateException("QubitSDK is not initialized yet. "
          + "Call QubitSDK.initialization().{...}.start() before any other call to QubitSDK");
    }
  }

  /**
   * Fetch experiences defined for given criteria.
   */
  public static void getExperiences(
      @NotNull List<Integer> experienceIdList,
      @NotNull Function1<? super List<? extends Experience>, Unit> onSuccess,
      @NotNull Function1<? super Throwable, Unit> onError,
      @Nullable Integer variation,
      @Nullable Boolean preview,
      @Nullable Boolean ignoreSegments
  ) {
    sdkSingleton.getExperienceInteractor()
        .fetchExperience(onSuccess, onError, experienceIdList, variation, preview, ignoreSegments);
  }

  /**
   * Fetch placement defined for given criteria.
   * Method is asynchronous, result is returned through passed callback functions.
   *
   * @param placementId    The unique ID of the placement.
   * @param mode           The mode to fetch placements content with. Defaults to [PlacementMode.LIVE]
   * @param attributes     JSON containing custom attributes to be used to query for the placement. "visitor" attribute will be ignored as it is set by SDK.
   * @param previewOptions Additional criteria used to query for the placement.
   * @param onSuccess      Callback invoked when query succeeds. Contains [Placement] object or null if no placement meets given criteria.
   * @param onError        Callback invoked when query fails.
   */
  public static void getPlacement(
      @NotNull String placementId,
      @Nullable PlacementMode mode,
      @Nullable JsonObject attributes,
      @NotNull PlacementPreviewOptions previewOptions,
      @NotNull Function1<? super Placement, Unit> onSuccess,
      @NotNull Function1<? super Throwable, Unit> onError
  ) {
    sdkSingleton.getPlacementInteractor()
        .fetchPlacement(placementId, mode, attributes, previewOptions, onSuccess, onError);
  }

}
