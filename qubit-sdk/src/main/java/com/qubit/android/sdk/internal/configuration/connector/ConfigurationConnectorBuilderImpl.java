package com.qubit.android.sdk.internal.configuration.connector;

import com.qubit.android.sdk.internal.common.util.UrlUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigurationConnectorBuilderImpl implements ConfigurationConnectorBuilder {

  private final String trackingId;

  public ConfigurationConnectorBuilderImpl(String trackingId) {
    this.trackingId = trackingId;
  }

  @Override
  public ConfigurationConnector buildFor(String endpointUrl) {
    return new ConfigurationConnectorImpl(trackingId, createConnector(endpointUrl));
  }


  private ConfigurationAPI createConnector(String endpointUrl) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(UrlUtils.addProtocol(endpointUrl, true))
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(ConfigurationAPI.class);
  }

}
