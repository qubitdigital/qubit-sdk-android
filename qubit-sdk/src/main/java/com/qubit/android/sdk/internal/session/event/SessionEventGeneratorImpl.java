package com.qubit.android.sdk.internal.session.event;

import android.os.Build;
import com.qubit.android.sdk.internal.lookup.LookupData;
import com.qubit.android.sdk.internal.session.SessionData;
import com.qubit.android.sdk.internal.session.model.SessionEvent;

import static com.qubit.android.sdk.internal.common.util.Elvis.*;

public class SessionEventGeneratorImpl implements SessionEventGenerator {

  private static final double MIN_TABLET_SIZE_IN = 7.0;

  private final ScreenSizeProvider screenSizeProvider;
  private final AppPropertiesProvider appPropertiesProvider;
  private final String deviceName = getIfNotNull(Build.MANUFACTURER, "") + "/" + getIfNotNull(Build.MODEL, "");

  public SessionEventGeneratorImpl(ScreenSizeProvider screenSizeProvider, AppPropertiesProvider appPropertiesProvider) {
    this.screenSizeProvider = screenSizeProvider;
    this.appPropertiesProvider = appPropertiesProvider;
  }

  @Override
  public SessionEvent generateSessionEvent(SessionData sessionData, LookupData lookupData) {
    SessionEvent sessionEvent = new SessionEvent();
    sessionEvent.setDeviceType(screenSizeProvider.getSizeInches() >= MIN_TABLET_SIZE_IN ? "tablet" : "mobile");
    sessionEvent.setDeviceName(deviceName);
    sessionEvent.setScreenWidth(screenSizeProvider.getWidthPx());
    sessionEvent.setScreenHeight(screenSizeProvider.getHeightPx());
    sessionEvent.setOsName("Android");
    sessionEvent.setOsVersion(Build.VERSION.RELEASE);
    sessionEvent.setAppType("app");
    sessionEvent.setAppName(appPropertiesProvider.getAppName());
    sessionEvent.setAppVersion(appPropertiesProvider.getAppVersion());

    if (lookupData != null) {
      sessionEvent.setFirstViewTs(lookupData.getFirstViewTs());
      sessionEvent.setLastViewTs(lookupData.getLastViewTs());
      sessionEvent.setFirstConversionTs(lookupData.getFirstConversionTs());
      sessionEvent.setLastConversionTs(lookupData.getLastConversionTs());
      sessionEvent.setIpLocation(lookupData.getIpLocation());
      sessionEvent.setIpAddress(lookupData.getIpAddress());
    }

    return sessionEvent;
  }


}
