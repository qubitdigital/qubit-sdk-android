package com.qubit.android.sdk.api;

import android.app.Application;
import android.content.Context;
import android.util.TimingLogger;
import com.qubit.android.sdk.api.logging.QBLogLevel;
import com.qubit.android.sdk.internal.SDK;
import com.qubit.android.sdk.internal.logging.QBLogger;

/**
 * Fluent interface for initialization of SDK, allowing to set configuration parameters and start SDK.
 */
public class InitializationBuilder {

  interface SdkConsumer {
    void accept(SDK sdk);
  }

  private final SdkConsumer sdkConsumer;
  private Context appContext;
  private String trackingId;
  private QBLogLevel logLevel;

  InitializationBuilder(SdkConsumer sdkConsumer) {
    this.sdkConsumer = sdkConsumer;
  }

  /**
   * Set context of Android application. I can be {@link Application} itself
   * or result of {@link Context#getApplicationContext()}.
   * @param context Android application context
   * @return Next step builder.
   */
  public ContextSetInitializationBuilder inAppContext(Context context) {
    this.appContext = context;
    return new ContextSetInitializationBuilder();
  }


  public class ContextSetInitializationBuilder {
    /**
     * Set tracking id.
     * @param trackingId Tracking id
     * @return Next step builder.
     */
    public MandatoryPropertiesSetInitializationBuilder withTrackingId(String trackingId) {
      InitializationBuilder.this.trackingId = trackingId;
      return new MandatoryPropertiesSetInitializationBuilder();
    }
  }


  public class MandatoryPropertiesSetInitializationBuilder {

    /**
     * Sets minimal level of logs emitted to the Android Logcat.
     * It is optional parameter. Default value is {@link QBLogLevel#WARN}.
     * @param logLevel Log level
     * @return Next step builder.
     */
    public MandatoryPropertiesSetInitializationBuilder withLogLevel(QBLogLevel logLevel) {
      InitializationBuilder.this.logLevel = logLevel;
      return this;
    }

    /**
     * Initiates and starts SDK.
     */
    public void start() {
      TimingLogger timings = new TimingLogger("qb-sdk", "initialization");
      timings.reset();

      if (logLevel != null) {
        QBLogger.logLevel = logLevel;
      }

      SDK sdk = new SDK(appContext, trackingId);
      timings.addSplit("creation");
      sdk.start();
      timings.addSplit("starting");
      sdkConsumer.accept(sdk);
      timings.addSplit("finishing");
      timings.dumpToLog();
    }
  }

}
