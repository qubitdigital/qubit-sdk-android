package com.qubit.android.sdk.internal.lookup.repository;

import com.qubit.android.sdk.internal.lookup.model.LookupModel;

public interface LookupRepository {

  void save(LookupModel lookupModel);
  LookupModel load();

}
