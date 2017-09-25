package com.qubit.android.sdk.internal.common.util;

public final class Elvis {

  private Elvis() {

  }

  public static String getIfNotEmpty(String string, String inCaseNull) {
    return string != null && !string.isEmpty() ? string : inCaseNull;
  }

  public static <E> E getIfNotNull(E ob, E inCaseNull) {
    return ob != null ? ob : inCaseNull;
  }

}
