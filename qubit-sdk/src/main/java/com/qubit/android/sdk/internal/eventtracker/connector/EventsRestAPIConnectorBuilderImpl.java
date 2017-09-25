package com.qubit.android.sdk.internal.eventtracker.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qubit.android.sdk.internal.common.util.UrlUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventsRestAPIConnectorBuilderImpl implements EventsRestAPIConnectorBuilder {

  private final String trackingId;
  private Gson gson;

  public EventsRestAPIConnectorBuilderImpl(String trackingId) {
    this.trackingId = trackingId;
  }

  @Override
  public EventsRestAPIConnector buildFor(String endpointUrl) {
    if (gson == null) {
      gson = createCustomGson();
    }
    return new EventsRestAPIConnectorImpl(trackingId, createConnector(endpointUrl));
  }


  private static Gson createCustomGson() {
    return new GsonBuilder()
        .registerTypeAdapter(EventRestModel.class, new EventRestModel.Serializer())
        .create();
  }

  private EventsRestAPI createConnector(String endpointUrl) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(UrlUtils.addProtocol(endpointUrl, true))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();

    return retrofit.create(EventsRestAPI.class);
  }

}
