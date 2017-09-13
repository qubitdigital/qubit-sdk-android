package com.qubit.android.sdk.internal.session.repository;

import com.qubit.android.sdk.internal.session.model.SessionDataModel;
import com.qubit.android.sdk.internal.util.Uninterruptibles;
import java.util.concurrent.TimeUnit;

public class SessionRepositoryImpl implements SessionRepository {

  @Override
  public void save(SessionDataModel sessionData) {

  }

  @Override
  public SessionDataModel load() {
    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
    return null;
  }

}
