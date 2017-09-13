package com.qubit.android.sdk.internal.session;

import java.util.concurrent.Future;

public interface SessionService {

  Future<SessionResponse> getOrCreateSession(String eventType, long nowEpochTimeMs);

}
