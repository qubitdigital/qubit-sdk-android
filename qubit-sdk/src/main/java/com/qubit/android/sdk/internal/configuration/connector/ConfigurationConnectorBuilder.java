package com.qubit.android.sdk.internal.configuration.connector;

public interface ConfigurationConnectorBuilder {
  ConfigurationConnector buildFor(String endpointUrl);
}
