package com.qubit.android.sdk.internal.lookup.connector;

import com.qubit.android.sdk.internal.lookup.model.LookupModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LookupAPI {

  @GET("/{trackingId}/{deviceId}")
  Call<LookupModel> getLookup(@Path("trackingId") String trackingId, @Path("deviceId") String deviceId);

}
