package com.qubit.android.sdk.internal.lookup.repository;

public interface LookupRepository {

  void save(LookupCache lookupCache);
  LookupCache load();

}
