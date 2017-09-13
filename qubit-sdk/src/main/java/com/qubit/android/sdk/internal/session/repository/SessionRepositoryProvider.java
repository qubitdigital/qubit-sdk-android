package com.qubit.android.sdk.internal.session.repository;

import android.os.Handler;

public interface SessionRepositoryProvider {
  SessionRepository provide(Handler handler);
}
