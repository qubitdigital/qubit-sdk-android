package com.qubit.android.sdk.internal.util;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class DateTimeUtils {

  private static final int SEC_IN_MIN = 60;
  private static final int MS_IN_SEC = 1000;

  private DateTimeUtils() {
  }

  public static long minToMs(long min) {
    return min * SEC_IN_MIN * MS_IN_SEC;
  }

  public static long secToMs(long secs) {
    return secs * MS_IN_SEC;
  }

  public static int getTimezoneOffsetMins() {
    TimeZone timeZone = new GregorianCalendar().getTimeZone();
    int offset = timeZone.getOffset(System.currentTimeMillis());
    return (int) TimeUnit.MINUTES.convert(offset, TimeUnit.MILLISECONDS);
  }
}
