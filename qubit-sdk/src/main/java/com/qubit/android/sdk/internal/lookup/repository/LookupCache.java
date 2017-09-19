package com.qubit.android.sdk.internal.lookup.repository;

import com.qubit.android.sdk.internal.lookup.model.LookupModel;

public class LookupCache {

  private LookupModel lookupModel;
  private long lastUpdateTimestamp;

  public LookupCache(LookupModel lookupModel, long lastUpdateTimestamp) {
    this.lookupModel = lookupModel;
    this.lastUpdateTimestamp = lastUpdateTimestamp;
  }

  public LookupModel getLookupModel() {
    return lookupModel;
  }

  public void setLookupModel(LookupModel lookupModel) {
    this.lookupModel = lookupModel;
  }

  public long getLastUpdateTimestamp() {
    return lastUpdateTimestamp;
  }

  public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
    this.lastUpdateTimestamp = lastUpdateTimestamp;
  }

  @Override
  public String toString() {
    return "LookupCache{"
        + "lookupModel=" + lookupModel
        + ", lastUpdateTimestamp=" + lastUpdateTimestamp
        + '}';
  }
}
