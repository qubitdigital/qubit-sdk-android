package com.qubit.android.sdk.internal.initialization;

import android.content.Context;
import android.provider.Settings;

public class SecureAndroidIdDeviceIdProvider implements DeviceIdProvider {

  private final String deviceId;

  public SecureAndroidIdDeviceIdProvider(Context context) {
    // TODO
    deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  @Override
  public String getDeviceId() {
    return deviceId;
  }
}
