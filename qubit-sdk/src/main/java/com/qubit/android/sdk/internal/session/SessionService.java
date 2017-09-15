package com.qubit.android.sdk.internal.session;

import java.util.concurrent.Future;

public interface SessionService {

  Future<SessionForEvent> getSessionDataForNextEvent(String eventType, long nowEpochTimeMs);

}
