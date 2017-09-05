package com.qubit.android.sdk.internal.eventtracker.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventsRestAPIConnectorBuilderImpl implements EventsRestAPIConnectorBuilder {

  public static final String URL_PREFIX_HTTP = "http://";
  public static final String URL_PREFIX_HTTPS = "https://";
  private final Gson gson;
  private final String trackingId;

  public EventsRestAPIConnectorBuilderImpl(String trackingId) {
    gson = createCustomGson();
    this.trackingId = trackingId;
  }

  @Override
  public EventsRestAPIConnector buildFor(String endpointUrl) {
    return new EventsRestAPIConnectorImpl(trackingId, createConnector(endpointUrl));
  }


  private static Gson createCustomGson() {
    return new GsonBuilder()
        .registerTypeAdapter(EventRestModel.class, new EventRestModel.Serializer())
        .create();
  }

  private EventsRestAPI createConnector(String endpointUrl) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(addProtocol(endpointUrl))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();

    return retrofit.create(EventsRestAPI.class);
  }

  private static String addProtocol(String endpoint) {
    if (endpoint.startsWith(URL_PREFIX_HTTP) || endpoint.startsWith(URL_PREFIX_HTTPS)) {
      return endpoint;
    } else {
      // TODO Change to https
      return URL_PREFIX_HTTP + endpoint;
    }
  }

}
