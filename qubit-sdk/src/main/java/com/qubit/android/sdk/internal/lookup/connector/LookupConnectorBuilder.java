package com.qubit.android.sdk.internal.lookup.connector;

public interface LookupConnectorBuilder {
  LookupConnector buildFor(String endpointUrl);
}
