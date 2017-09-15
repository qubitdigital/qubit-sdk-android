package com.qubit.android.sdk.internal.session;

import com.qubit.android.sdk.api.tracker.event.QBEvent;

public interface NewSessionRequest {
  QBEvent getSessionEvent();
  SessionData getSessionData();
}
