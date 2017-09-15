package com.qubit.android.sdk.internal.eventtracker;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTypeTransformerTest {
  
  @Test
  public void transform() throws Exception {
    assertEquals("ecView", transform("ecView", null, null));
    assertEquals("ecView", transform("ecView", "", ""));

    assertEquals("ecView", transform("ecView", "", "ec"));
    assertEquals("n.ecView", transform("n.ecView", "", "ec"));
    assertEquals("ecView", transform("View", "", "ec"));
    assertEquals("ecView", transform("view", "", "ec"));
    assertEquals("ns.view", transform("ns.view", "", "ec"));
    assertEquals("ecV", transform("v", "", "ec"));

    assertEquals("ns1.ecView", transform("ns1.ecView", "ns", null));
    assertEquals("qubit.ecView", transform("qubit.ecView", "ns", null));
    assertEquals("ns.ecView", transform("ecView", "ns", null));

    assertEquals("ns.ecView", transform("view", "ns", "ec"));

  }
  
  private String transform(String sourceType, String namespace, String vertical) {
    return new EventTypeTransformer(namespace, vertical).transform(sourceType);
  }

}
