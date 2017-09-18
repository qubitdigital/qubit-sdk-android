package com.qubit.android.sdk.internal.eventtracker.connector;

import com.qubit.android.sdk.internal.logging.QBLogger;
import java.io.IOException;
import java.util.List;
import retrofit2.Response;

public class EventsRestAPIConnectorImpl implements EventsRestAPIConnector {

  private static final QBLogger LOGGER = QBLogger.getFor("EventsRestAPIConnector");

  private static final EventRestModel[] EMPTY_EVENT_LIST = new EventRestModel[0];
  private static final int STATUS_CODE_OK = 200;
  private static final int STATUS_CODE_BACKEND_ERROR = 500;

  private final String trackingId;
  private final EventsRestAPI api;

  public EventsRestAPIConnectorImpl(String trackingId, EventsRestAPI api) {
    this.trackingId = trackingId;
    this.api = api;
  }

  @SuppressWarnings("checkstyle:illegalcatch")
  @Override
  public ResponseStatus sendEvents(List<EventRestModel> events, boolean dedupe) {
    if (api == null) {
      throw new IllegalStateException("EventSender: Before sending events, endpoint url has to be set. "
          + "Use setEndpointUrl method");
    }
    try {
      Response<RestApiResponse> response =
          api.sendEvents(trackingId, dedupe, events.toArray(EMPTY_EVENT_LIST)).execute();

      RestApiResponse responseBody = response.body();
      if (responseBody == null) {
        LOGGER.e("Response doesn't contain body. Will be retried.");
        return ResponseStatus.RETRYABLE_ERROR;
      }
      Status status = responseBody.getStatus();
      if (status == null) {
        LOGGER.e("Response doesn't contain status property. Will be retried.");
        return ResponseStatus.RETRYABLE_ERROR;
      }

      int statusCode = status.getCode();
      if (statusCode == STATUS_CODE_OK) {
        return ResponseStatus.OK;
      } else if (statusCode >= STATUS_CODE_BACKEND_ERROR) {
        logResponseError(status, true);
        return ResponseStatus.RETRYABLE_ERROR;
      } else {
        logResponseError(status, false);
        return ResponseStatus.ERROR;
      }
    } catch (IOException e) {
      LOGGER.e("Error connecting to server. Will be retried", e);
      return ResponseStatus.RETRYABLE_ERROR;
    } catch (RuntimeException e) {
      LOGGER.e("Unexpected exception while sending events. Will not be retried", e);
      return ResponseStatus.ERROR;
    }
  }

  private static void logResponseError(Status status, boolean willRetry) {
    LOGGER.e("Server responded with status: " + status + " message: " + status.getMessage() + ". "
        + (willRetry ? "Request will be retried." : "Request will not be retried."));
  }

}
