package com.qubit.android.sdk.internal.session.model;

import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.internal.session.SessionData;
import com.qubit.android.sdk.internal.session.SessionResponse;


public class SessionResponseImpl implements SessionResponse {

  private final SessionData sessionData;
  private final QBEvent sessionEvent;

  public SessionResponseImpl(SessionData sessionData, QBEvent sessionEvent) {
    this.sessionData = sessionData;
    this.sessionEvent = sessionEvent;
  }

  @Override
  public boolean isNewSession() {
    return sessionEvent != null;
  }

  @Override
  public SessionData getSessionData() {
    return sessionData;
  }

  @Override
  public QBEvent getSessionEvent() {
    return sessionEvent;
  }
}
