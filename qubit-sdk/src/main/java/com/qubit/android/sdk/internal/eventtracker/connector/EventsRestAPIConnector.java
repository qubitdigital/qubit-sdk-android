package com.qubit.android.sdk.internal.eventtracker.connector;

import com.qubit.android.sdk.internal.configuration.ConfigurationResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventsRestAPIConnector {

  @POST("/events/raw/{trackingId}")
  Call<RestApiResponse> sendEvents(@Path("trackingId") String trackingId);

}
