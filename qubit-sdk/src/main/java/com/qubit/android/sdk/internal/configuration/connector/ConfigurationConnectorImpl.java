package com.qubit.android.sdk.internal.configuration.connector;

import com.qubit.android.sdk.internal.common.logging.QBLogger;
import java.io.IOException;
import java.net.HttpURLConnection;
import retrofit2.Response;

public class ConfigurationConnectorImpl implements ConfigurationConnector {

  private static final QBLogger LOGGER = QBLogger.getFor("ConfigurationConnector");

  private final String trackingId;
  private final ConfigurationAPI api;

  public ConfigurationConnectorImpl(String trackingId, ConfigurationAPI api) {
    this.trackingId = trackingId;
    this.api = api;
  }

  @Override
  public ConfigurationResponse download() {
    try {
      Response<ConfigurationRestModel> response = api.download(trackingId).execute();
      if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
        LOGGER.w("Configuration not found");
        return ConfigurationResponse.notFound();
      }
      if (!response.isSuccessful() || response.errorBody() != null || response.body() == null) {
        LOGGER.e("Failed to download configuration");
        return ConfigurationResponse.error();
      }

      LOGGER.d("Configuration downloaded");
      return ConfigurationResponse.ok(response.body());
    } catch (IOException e) {
      LOGGER.e("Failed to download configuration: ", e);
      return ConfigurationResponse.error();
    }
  }
}
