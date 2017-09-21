package com.qubit.android.sdk.internal.eventtracker;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTypeTransformerTest {
  
  @Test
  public void transform() throws Exception {
    assertEquals("ecView", transform("ecView", null));
    assertEquals("ecView", transform("ecView", ""));

    assertEquals("n.ecView", transform("n.ecView", ""));
    assertEquals("ns.view", transform("ns.view", ""));

    assertEquals("ns1.ecView", transform("ns1.ecView", "ns"));
    assertEquals("qubit.ecView", transform("qubit.ecView", "ns"));
    assertEquals("ns.ecView", transform("ecView", "ns"));
  }
  
  private String transform(String sourceType, String namespace) {
    return new EventTypeTransformer(namespace).transform(sourceType);
  }

}
