package com.qubit.android.sdk.internal.session.event;

import android.os.Build;
import com.qubit.android.sdk.internal.session.SessionData;
import com.qubit.android.sdk.internal.session.model.SessionEvent;

import static com.qubit.android.sdk.internal.util.Elvis.*;

public class SessionEventGeneratorImpl implements SessionEventGenerator {

  private static final double MIN_TABLET_SIZE_IN = 7.0;

  private final ScreenSizeProvider screenSizeProvider;
  private final String deviceName = getIfNotNull(Build.MANUFACTURER, "") + "/" + getIfNotNull(Build.MODEL, "");
  private final String deviceType;

  public SessionEventGeneratorImpl(ScreenSizeProvider screenSizeProvider) {
    this.screenSizeProvider = screenSizeProvider;
    this.deviceType = screenSizeProvider.getSizeInches() >= MIN_TABLET_SIZE_IN ? "tablet" : "mobile";
  }

  @Override
  public SessionEvent generateSessionEvent(SessionData sessionData) {
    SessionEvent sessionEvent = new SessionEvent();
    sessionEvent.setDeviceType(deviceType);
    sessionEvent.setDeviceName(deviceName);
    sessionEvent.setScreenWidth(screenSizeProvider.getWidthPx());
    sessionEvent.setScreenHeight(screenSizeProvider.getHeightPx());
    sessionEvent.setOsName("Android");
    sessionEvent.setOsVersion(Build.VERSION.RELEASE);
    sessionEvent.setAppType("app");
    // TODO
    return sessionEvent;
  }


}
