package com.qubit.android.sdk.internal.util;

/**
 * Created by aamq on 30.08.2017.
 */

public final class Elvis {

  private Elvis() {

  }

  public static String getNotEmpty(String string, String inCaseNull) {
    return string != null && !string.isEmpty() ? string : inCaseNull;
  }

  public static <E> E get(E ob, E inCaseNull) {
    return ob != null ? ob : inCaseNull;
  }

}
