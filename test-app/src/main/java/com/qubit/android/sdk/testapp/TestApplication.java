package com.qubit.android.sdk.testapp;

import android.app.Application;
import android.util.Log;
import com.qubit.android.sdk.api.QubitSDK;
import com.qubit.android.sdk.api.initialization.LogLevel;

public class TestApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i("qb-testapp", "Test application initializes");

    QubitSDK.initialization()
        .inAppContext(this)
        .withTrackingId("miquido")
        .withLogLevel(LogLevel.DEBUG)
        .start();
  }

}
