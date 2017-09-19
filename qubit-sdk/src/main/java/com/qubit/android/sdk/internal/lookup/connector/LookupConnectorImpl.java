package com.qubit.android.sdk.internal.lookup.connector;

import com.qubit.android.sdk.internal.lookup.model.LookupModel;

public class LookupConnectorImpl implements LookupConnector {

  private final String trackingId;
  private final String deviceId;
  private final LookupAPI lookupAPI;

  public LookupConnectorImpl(String trackingId, String deviceId, LookupAPI lookupAPI) {
    this.trackingId = trackingId;
    this.deviceId = deviceId;
    this.lookupAPI = lookupAPI;
  }

  @Override
  public LookupModel getLookupData() {
    // TODO
    return null;
  }
}
