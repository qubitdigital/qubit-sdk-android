package com.qubit.android.sdk.testapp;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;
import com.qubit.android.sdk.api.QubitSDK;
import com.qubit.android.sdk.api.logging.QBLogLevel;
import com.qubit.android.sdk.internal.configuration.ConfigurationServiceImpl;

public class TestApplication extends Application {

  public static final String TAG = "qb-testapp";

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "Test application initializes");

    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork()   // or .detectAll() for all detectable problems
        .penaltyLog()
        .build());

    // Only for debugging purposes. It is not officially public API. DO NOT USE IT.
    //ConfigurationServiceImpl.configurationUrl = "http://s3-eu-west-1.amazonaws.com/ljazgar-qubit/config/";
    //ConfigurationServiceImpl.enforceDownloadOnStart = false;

    QubitSDK.initialization()
        .inAppContext(this)
        .withTrackingId("miquido")
        .withLogLevel(QBLogLevel.DEBUG)
        .start();

    Log.i(TAG, "Test application initialized");
  }

}
