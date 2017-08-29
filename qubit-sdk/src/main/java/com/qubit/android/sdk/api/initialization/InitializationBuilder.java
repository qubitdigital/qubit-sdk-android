package com.qubit.android.sdk.api.initialization;

import android.content.Context;
import com.qubit.android.sdk.internal.SDK;

public class InitializationBuilder {

  private Context appContext;
  private String trackingId;
  private QBLogLevel logLevel;

  public InitializationBuilder() {
  }

  public ContextSetInitializationBuilder inAppContext(Context context) {
    this.appContext = context;
    return new ContextSetInitializationBuilder();
  }


  public class ContextSetInitializationBuilder {
    public MandatoryPropertiesSetInitializationBuilder withTrackingId(String trackingId) {
      InitializationBuilder.this.trackingId = trackingId;
      return new MandatoryPropertiesSetInitializationBuilder();
    }
  }


  public class MandatoryPropertiesSetInitializationBuilder {

    public MandatoryPropertiesSetInitializationBuilder withLogLevel(QBLogLevel logLevel) {
      InitializationBuilder.this.logLevel = logLevel;
      return this;
    }

    public void start() {
      SDK.logLevel = logLevel;
      // TODO
    }
  }

}
