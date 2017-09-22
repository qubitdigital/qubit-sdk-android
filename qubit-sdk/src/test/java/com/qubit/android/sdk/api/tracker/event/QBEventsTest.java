package com.qubit.android.sdk.api.tracker.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.is;

public class QBEventsTest {

  private static final String EVENT_TYPE = "ecView";
  private static final String KEY = "viewId";
  private static final String VALUE = "button";

  private JsonObject correctEvent;
  private JsonObject emptyEvent = new JsonObject();

  private String jsonStringEvent = "{ \"" + KEY + "\" : \"" + VALUE + "\" }";
  private EventWithField objectEventWithField = new EventWithField(VALUE);
  private EventNoFields beanEventWithNoFields = new EventNoFields();
  private JsonObject jsonObjectEvent = new Gson().toJsonTree(objectEventWithField).getAsJsonObject();
  private Map<String, Object> mapEvent;

  private static class EventWithField {
    String viewId;

    EventWithField(String viewId) {
      this.viewId = viewId;
    }
  }

  private static class EventNoFields {
  }

  private static class ComplexEvent {
    String viewId;
    List<Integer> clicksAmount;
    Map<Integer, Boolean> mysterious;
    Float clickLength;
    EventWithField details;

    ComplexEvent(String viewId, List<Integer> clicksAmount, Map<Integer, Boolean> mysterious, Float clickLength,
                 EventWithField details) {
      this.viewId = viewId;
      this.clicksAmount = clicksAmount;
      this.mysterious = mysterious;
      this.clickLength = clickLength;
      this.details = details;
    }
  }

  @Before
  public void setUp() throws Exception {
    correctEvent = new JsonObject();
    correctEvent.addProperty(KEY, VALUE);

    mapEvent = new HashMap<>();
    mapEvent.put(KEY, VALUE);
  }

  @Test
  public void fromJsonString_correct() {
    assertThat(QBEvents.fromJsonString(EVENT_TYPE, jsonStringEvent).toJsonObject(), is(equalTo(correctEvent)));
  }

  @Test(expected = NullPointerException.class)
  public void fromJsonString_null() {
    QBEvents.fromJsonString(EVENT_TYPE, null);
  }

  @Test(expected = QBEvents.JsonParseException.class)
  public void fromJsonString_empty() {
    QBEvents.fromJsonString(EVENT_TYPE, "");
  }

  @Test(expected = QBEvents.JsonParseException.class)
  public void fromJsonString_malformedJsonString() {
    String malformedJsonString = "{ \"viewId\" \"button\" }";
    QBEvents.fromJsonString(EVENT_TYPE, malformedJsonString);
  }

  @Test(expected = QBEvents.JsonParseException.class)
  public void fromJsonString_nonJsonString() {
    String nonJsonString = "abc";
    QBEvents.fromJsonString(EVENT_TYPE, nonJsonString);
  }

  @Test(expected = QBEvents.JsonParseException.class)
  public void fromJsonString_nonJsonObject() {
    String nonJsonString = "123";
    QBEvents.fromJsonString(EVENT_TYPE, nonJsonString);
  }

  @Test
  public void fromJson_correct() {
    assertThat(QBEvents.fromJson(EVENT_TYPE, jsonObjectEvent).toJsonObject(), is(equalTo(correctEvent)));
  }

  @Test(expected = NullPointerException.class)
  public void fromJson_null() {
    QBEvents.fromJson(EVENT_TYPE, null);
  }

  @Test
  public void fromObject_correctSimple() {
    assertThat(QBEvents.fromObject(EVENT_TYPE, objectEventWithField).toJsonObject(), is(equalTo(correctEvent)));
  }

  @Test
  public void fromObject_correctComplex() {
    Map<Integer, Boolean> map = new HashMap<>();
    map.put(7, false);
    map.put(8, true);

    ComplexEvent complexEvent = new ComplexEvent(VALUE, Arrays.asList(2, 76, 3), map, 2.65F,
        new EventWithField("switch"));
    JsonObject correctComplexEvent = new JsonParser()
        .parse("{\"viewId\":\"button\",\"clicksAmount\":[2,76,3],\"mysterious\":{\"7\":false,\"8\":true},"
            + "\"clickLength\":2.65,\"details\":{\"viewId\":\"switch\"}}").getAsJsonObject();

    assertThat(QBEvents.fromObject(EVENT_TYPE, complexEvent).toJsonObject().toString(),
        is(equalTo(correctComplexEvent.toString())));
  }

  @Test(expected = NullPointerException.class)
  public void fromObject_null() {
    QBEvents.fromObject(EVENT_TYPE, null);
  }

  @Test
  public void fromBean_withNoFields() {
    assertThat(QBEvents.fromObject(EVENT_TYPE, beanEventWithNoFields).toJsonObject(), is(equalTo(emptyEvent)));
  }

  @Test
  public void fromMap_correct() {
    assertThat(QBEvents.fromMap(EVENT_TYPE, mapEvent).toJsonObject(), is(equalTo(correctEvent)));
  }

  @Test(expected = NullPointerException.class)
  public void fromMap_null() {
    QBEvents.fromMap(EVENT_TYPE, null);
  }

  @Test
  public void fromMap_empty() {
    Map<String, Object> emptyMap = Collections.emptyMap();
    assertThat(QBEvents.fromMap(EVENT_TYPE, emptyMap).toJsonObject(), is(equalTo(emptyEvent)));
  }

}
