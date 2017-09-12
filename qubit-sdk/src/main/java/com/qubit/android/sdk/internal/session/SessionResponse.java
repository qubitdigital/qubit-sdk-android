package com.qubit.android.sdk.internal.session;

import com.qubit.android.sdk.api.tracker.event.QBEvent;

public interface SessionResponse {

  boolean isNewSession();
  SessionData getSessionData();
  QBEvent getSessionEvent();

}
