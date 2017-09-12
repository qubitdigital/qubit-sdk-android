package com.qubit.android.sdk.internal.session.event;

import com.qubit.android.sdk.internal.session.SessionData;
import com.qubit.android.sdk.internal.session.model.SessionEvent;

public class SessionEventGeneratorImpl implements SessionEventGenerator {

  @Override
  public SessionEvent generateSessionEvent(SessionData sessionData) {
    SessionEvent sessionEvent = new SessionEvent();
    sessionEvent.setDeviceType("mobile");
    sessionEvent.setAppType("app");
    sessionEvent.setOsName("Android");
    return sessionEvent;
  }


}
