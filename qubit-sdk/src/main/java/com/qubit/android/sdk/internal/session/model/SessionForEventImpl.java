package com.qubit.android.sdk.internal.session.model;

import com.qubit.android.sdk.internal.session.NewSessionRequest;
import com.qubit.android.sdk.internal.session.SessionData;
import com.qubit.android.sdk.internal.session.SessionForEvent;


public class SessionForEventImpl implements SessionForEvent {

  private final SessionData sessionData;
  private final NewSessionRequest newSessionRequest;

  public SessionForEventImpl(SessionData sessionData) {
    this.sessionData = sessionData;
    this.newSessionRequest = null;
  }

  public SessionForEventImpl(SessionData sessionData, NewSessionRequest newSessionRequest) {
    this.sessionData = sessionData;
    this.newSessionRequest = newSessionRequest;
  }

  @Override
  public SessionData getEventSessionData() {
    return sessionData;
  }

  @Override
  public NewSessionRequest getNewSessionRequest() {
    return newSessionRequest;
  }

}
