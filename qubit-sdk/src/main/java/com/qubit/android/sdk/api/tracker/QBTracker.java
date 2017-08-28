package com.qubit.android.sdk.api.tracker;

import com.qubit.android.sdk.api.tracker.event.QBEvent;

public interface QBTracker {

  void sendEvent(String type, QBEvent event);

  void enable();
  void enable(boolean enable);
  void disable();

}
