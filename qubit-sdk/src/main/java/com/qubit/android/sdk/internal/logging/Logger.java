package com.qubit.android.sdk.internal.logging;

import android.util.Log;
import com.qubit.android.sdk.api.logging.QBLogLevel;
import com.qubit.android.sdk.internal.SDK;

import static com.qubit.android.sdk.api.logging.QBLogLevel.DEBUG;
import static com.qubit.android.sdk.api.logging.QBLogLevel.ERROR;
import static com.qubit.android.sdk.api.logging.QBLogLevel.INFO;
import static com.qubit.android.sdk.api.logging.QBLogLevel.VERBOSE;
import static com.qubit.android.sdk.api.logging.QBLogLevel.WARN;

public final class Logger {

  private Logger() {
  }

  public static void e(String tag, String message) {
    if (shouldShowLog(ERROR)) {
      Log.e(tag, message);
    }
  }

  public static void w(String tag, String message) {
    if (shouldShowLog(WARN)) {
      Log.w(tag, message);
    }
  }

  public static void i(String tag, String message) {
    if (shouldShowLog(INFO)) {
      Log.i(tag, message);
    }
  }

  public static void d(String tag, String message) {
    if (shouldShowLog(DEBUG)) {
      Log.d(tag, message);
    }
  }

  public static void v(String tag, String message) {
    if (shouldShowLog(VERBOSE)) {
      Log.v(tag, message);
    }
  }

  private static boolean shouldShowLog(QBLogLevel qbLogLevel) {
    return SDK.logLevel.compareTo(qbLogLevel) >= 0;
  }

}
