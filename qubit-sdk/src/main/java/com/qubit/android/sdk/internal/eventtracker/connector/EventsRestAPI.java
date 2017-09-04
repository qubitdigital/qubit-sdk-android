package com.qubit.android.sdk.internal.eventtracker.connector;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventsRestAPI {

  @POST("/events/raw/{trackingId}")
  Call<RestApiResponse> sendEvents(@Path("trackingId") String trackingId, @Query("dedupe") boolean dedupe,
                                   @Body EventRestModel[] events);

}
