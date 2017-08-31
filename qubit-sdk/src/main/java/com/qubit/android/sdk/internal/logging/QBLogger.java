package com.qubit.android.sdk.internal.logging;

import android.support.annotation.NonNull;
import android.util.Log;
import com.qubit.android.sdk.api.logging.QBLogLevel;
import com.qubit.android.sdk.internal.SDK;

import static com.qubit.android.sdk.api.logging.QBLogLevel.DEBUG;
import static com.qubit.android.sdk.api.logging.QBLogLevel.ERROR;
import static com.qubit.android.sdk.api.logging.QBLogLevel.INFO;
import static com.qubit.android.sdk.api.logging.QBLogLevel.VERBOSE;
import static com.qubit.android.sdk.api.logging.QBLogLevel.WARN;

public final class QBLogger {

  private static final String TAG = "qb-sdk";

  private final String component;

  private QBLogger(String component) {
    this.component = component;
  }

  public static QBLogger getFor(String component) {
    return new QBLogger(component);
  }

  public void e(String message) {
    if (shouldShowLog(ERROR)) {
      Log.e(TAG, createMessageWithComponent(message));
    }
  }

  public void e(String message, Throwable throwable) {
    if (shouldShowLog(ERROR)) {
      Log.e(TAG, createMessageWithComponent(message), throwable);
    }
  }

  public void w(String message) {
    if (shouldShowLog(WARN)) {
      Log.w(TAG, createMessageWithComponent(message));
    }
  }

  public void w(String message, Throwable throwable) {
    if (shouldShowLog(WARN)) {
      Log.w(TAG, createMessageWithComponent(message), throwable);
    }
  }

  public void i(String message) {
    if (shouldShowLog(INFO)) {
      Log.i(TAG, createMessageWithComponent(message));
    }
  }

  public void i(String message, Throwable throwable) {
    if (shouldShowLog(INFO)) {
      Log.i(TAG, createMessageWithComponent(message), throwable);
    }
  }

  public void d(String message) {
    if (shouldShowLog(DEBUG)) {
      Log.d(TAG, createMessageWithComponent(message));
    }
  }

  public void d(String message, Throwable throwable) {
    if (shouldShowLog(DEBUG)) {
      Log.d(TAG, createMessageWithComponent(message), throwable);
    }
  }

  public void v(String message) {
    if (shouldShowLog(VERBOSE)) {
      Log.v(TAG, createMessageWithComponent(message));
    }
  }

  public void v(String message, Throwable throwable) {
    if (shouldShowLog(VERBOSE)) {
      Log.v(TAG, createMessageWithComponent(message), throwable);
    }
  }

  private static boolean shouldShowLog(QBLogLevel qbLogLevel) {
    return SDK.logLevel.compareTo(qbLogLevel) >= 0;
  }

  @NonNull
  private String createMessageWithComponent(String message) {
    return component + ": " + message;
  }

}
