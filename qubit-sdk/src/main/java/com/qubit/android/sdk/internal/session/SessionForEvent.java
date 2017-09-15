package com.qubit.android.sdk.internal.session;

public interface SessionForEvent {

  NewSessionRequest getNewSessionRequest();
  SessionData getEventSessionData();

}
