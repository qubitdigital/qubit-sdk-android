package com.qubit.android.sdk.internal.eventtracker.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qubit.android.sdk.api.tracker.event.QBEvent;
import com.qubit.android.sdk.api.tracker.event.QBEvents;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Ignore
public class EventsRestAPITest {

  public static final String EVENT_TYPE = "ecView";
  public static final String TRACKING_ID = "miquido";
  private EventsRestAPI connector;

  private static final class EcViewEvent {
    private String type;

    private EcViewEvent(String type) {
      this.type = type;
    }
  }

  @Before
  public void before() {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(EventRestModel.class, new EventRestModel.Serializer())
        .create();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://gong-eb.qubit.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();

    connector = retrofit.create(EventsRestAPI.class);
  }

  @Test
  public void sendEvents() throws Exception {
    EventContext context = new EventContext("12345", 13);

    Object eventObject1 = new EcViewEvent("button1");
    QBEvent qbEvent1 = QBEvents.fromObject(EVENT_TYPE, eventObject1);
    EventMeta meta1 = new EventMeta("3", System.currentTimeMillis(), EVENT_TYPE, TRACKING_ID);
    EventRestModel eventRestModel1 = new EventRestModel(qbEvent1.toJsonObject(), meta1, context);

    Object eventObject2 = new EcViewEvent("button2");
    QBEvent qbEvent2 = QBEvents.fromObject(EVENT_TYPE, eventObject2);
    EventMeta meta2 = new EventMeta("4", System.currentTimeMillis(), EVENT_TYPE, TRACKING_ID);
    EventRestModel eventRestModel2 = new EventRestModel(qbEvent2.toJsonObject(), meta2, context);

    EventRestModel[] events = new EventRestModel[] { eventRestModel1, eventRestModel2};

    Response<RestApiResponse> response = connector.sendEvents(TRACKING_ID, false, events).execute();

    System.out.println("Response: Status:" + response.body().getStatus());
  }

}
