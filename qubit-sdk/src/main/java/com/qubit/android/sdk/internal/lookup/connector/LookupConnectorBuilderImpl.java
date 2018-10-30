package com.qubit.android.sdk.internal.lookup.connector;

import com.qubit.android.sdk.internal.common.util.UrlUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LookupConnectorBuilderImpl implements LookupConnectorBuilder {

  private final String trackingId;
  private final String deviceId;

  public LookupConnectorBuilderImpl(String trackingId, String deviceId) {
    this.trackingId = trackingId;
    this.deviceId = deviceId;
  }

  @Override
  public LookupConnector buildFor(String endpointUrl) {
    return new LookupConnectorImpl(trackingId, deviceId, createConnector(endpointUrl));
  }

  private LookupAPI createConnector(String endpointUrl) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(UrlUtils.addProtocol(endpointUrl, true))
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(LookupAPI.class);
  }
}
