package com.qubit.android.sdk.internal.eventtracker.connector;

public interface EventsRestAPIConnectorBuilder {
  EventsRestAPIConnector buildFor(String endpointUrl);
}
