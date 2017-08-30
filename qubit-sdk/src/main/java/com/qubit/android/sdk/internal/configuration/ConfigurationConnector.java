package com.qubit.android.sdk.internal.configuration;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface ConfigurationConnector {

  String TRACKING_ID_PATH = "/{id}.json";
  String TRACKING_ID_PARAM = "id";

  @GET("qubit-mobile-config" + TRACKING_ID_PATH)
  Call<ConfigurationResponse> download(@Path(TRACKING_ID_PARAM) String trackingId);

}
