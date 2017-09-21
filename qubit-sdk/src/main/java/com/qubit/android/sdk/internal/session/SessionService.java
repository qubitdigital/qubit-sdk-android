package com.qubit.android.sdk.internal.session;

import java.util.concurrent.Future;

public interface SessionService {

  String SESSION_EVENT_TYPE = "qubit.session";

  Future<SessionForEvent> getSessionDataForNextEvent(String eventType, long nowEpochTimeMs);

}
