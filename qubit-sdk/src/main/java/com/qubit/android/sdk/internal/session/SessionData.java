package com.qubit.android.sdk.internal.session;

public interface SessionData {

  long getSessionNumber();
  long getSessionTs();
  Long getViewNumber();
  Long getSessionViewNumber();
  Long getViewTs();
  long getLastEventTs();

}
