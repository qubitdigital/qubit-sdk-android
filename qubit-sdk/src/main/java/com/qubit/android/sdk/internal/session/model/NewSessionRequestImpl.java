package com.qubit.android.sdk.internal.session.model;

import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.internal.session.NewSessionRequest;


public class NewSessionRequestImpl implements NewSessionRequest {

  private final QBEvent sessionEvent;
  private final SessionDataModel sessionData;

  public NewSessionRequestImpl(QBEvent sessionEvent, SessionDataModel sessionData) {
    this.sessionEvent = sessionEvent;
    this.sessionData = sessionData;
  }

  @Override
  public QBEvent getSessionEvent() {
    return sessionEvent;
  }

  @Override
  public SessionDataModel getSessionData() {
    return sessionData;
  }

}
