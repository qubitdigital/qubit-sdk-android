package com.qubit.android.sdk.internal.configuration;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ConfigurationConnector {

  String CONFIGURATION_PATH = "{trackingId}.json";
  String TRACKING_ID_PARAM = "trackingId";

  @GET(CONFIGURATION_PATH)
  Call<ConfigurationResponse> download(@Path(TRACKING_ID_PARAM) String trackingId);

}
