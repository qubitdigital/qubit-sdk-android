package com.qubit.android.sdk.api.tracker;

import com.qubit.android.sdk.api.tracker.event.QBEvent;

public interface EventTracker {

  void sendEvent(QBEvent event);

  void enable(boolean enable);

}
