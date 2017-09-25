package com.qubit.android.sdk.internal.lookup.connector;

import com.qubit.android.sdk.internal.common.logging.QBLogger;
import com.qubit.android.sdk.internal.lookup.model.LookupModel;
import java.io.IOException;
import retrofit2.Response;

public class LookupConnectorImpl implements LookupConnector {

  private static final QBLogger LOGGER = QBLogger.getFor("LookupConnector");

  private final String trackingId;
  private final String deviceId;
  private final LookupAPI lookupAPI;

  public LookupConnectorImpl(String trackingId, String deviceId, LookupAPI lookupAPI) {
    this.trackingId = trackingId;
    this.deviceId = deviceId;
    this.lookupAPI = lookupAPI;
  }

  @SuppressWarnings("checkstyle:illegalcatch")
  @Override
  public LookupModel getLookupData() {
    try {
      Response<LookupModel> response = lookupAPI.getLookup(trackingId, deviceId).execute();

      LookupModel responseBody = response.body();
      if (responseBody == null) {
        LOGGER.e("Response doesn't contain body.");
      }
      return responseBody;
    } catch (IOException e) {
      LOGGER.e("Error connecting to server.", e);
      return null;
    } catch (RuntimeException e) {
      LOGGER.e("Unexpected exception while getting lookup.", e);
      return null;
    }
  }

}
