package com.qubit.android.sdk.internal.session.repository;

import com.qubit.android.sdk.internal.session.model.SessionDataModel;

public interface SessionRepository {

  void save(SessionDataModel sessionData);
  SessionDataModel load();

}
