package com.qubit.android.sdk.internal.lookup.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qubit.android.sdk.internal.util.UrlUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LookupConnectorBuilderImpl implements LookupConnectorBuilder {

  private final String trackingId;
  private final String deviceId;
  private Gson gson;

  public LookupConnectorBuilderImpl(String trackingId, String deviceId) {
    this.trackingId = trackingId;
    this.deviceId = deviceId;
  }


  @Override
  public LookupConnector buildFor(String endpointUrl) {
    if (gson == null) {
      gson = createCustomGson();
    }
    return new LookupConnectorImpl(trackingId, deviceId, createConnector(endpointUrl)) {
    };
  }


  private static Gson createCustomGson() {
    return new GsonBuilder().create();
  }

  private LookupAPI createConnector(String endpointUrl) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(UrlUtils.addProtocol(endpointUrl, true))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();

    return retrofit.create(LookupAPI.class);
  }

}
