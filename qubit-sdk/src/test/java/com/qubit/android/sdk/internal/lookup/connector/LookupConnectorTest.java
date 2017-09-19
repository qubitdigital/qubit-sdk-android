package com.qubit.android.sdk.internal.lookup.connector;

import com.qubit.android.sdk.internal.lookup.model.LookupModel;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class LookupConnectorTest {

  @Test
  public void getLookupData() throws Exception {
    LookupConnectorBuilder lookupConnectorBuilder = new LookupConnectorBuilderImpl("miquido", "d2c6e2d7025f13a1");
    LookupConnector lookupConnector = lookupConnectorBuilder.buildFor("lookup.qubit.com");

    LookupModel lookupModel = lookupConnector.getLookupData();

    System.out.println("LookupModel: " + lookupModel);
  }

}
