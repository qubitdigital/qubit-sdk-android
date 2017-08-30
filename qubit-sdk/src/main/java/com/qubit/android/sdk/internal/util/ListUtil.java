package com.qubit.android.sdk.internal.util;

import java.util.List;

public abstract class ListUtil {

  public static <T> List<T> firstElements(List<T> list, int max) {
    return list.subList(0, max < list.size() ? max : list.size());
  }
}
