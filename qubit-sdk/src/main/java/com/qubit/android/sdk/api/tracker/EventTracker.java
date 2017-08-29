package com.qubit.android.sdk.api.tracker;

import com.qubit.android.sdk.api.tracker.event.QBEvent;

public interface EventTracker {

  void sendEvent(String type, QBEvent event);

  void enable(boolean enable);

}
