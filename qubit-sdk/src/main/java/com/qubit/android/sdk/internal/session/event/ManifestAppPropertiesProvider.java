package com.qubit.android.sdk.internal.session.event;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ManifestAppPropertiesProvider implements AppPropertiesProvider {

  private final Context appContext;
  private String appName;
  private String appVersion;
  private boolean appVersionInitialized;

  public ManifestAppPropertiesProvider(Context appContext) {
    this.appContext = appContext;
  }

  @Override
  public String getAppName() {
    if (appName == null) {
      appName = getAppLabel(appContext);
    }
    return appName;
  }

  @Override
  public String getAppVersion() {
    if (!appVersionInitialized) {
      appVersion = getAppVersionName(appContext);
      appVersionInitialized = true;
    }
    return appVersion;
  }

  private static String getAppLabel(Context appContext) {
    PackageManager packageManager = appContext.getPackageManager();
    return appContext.getApplicationInfo().loadLabel(packageManager).toString();
  }

  private static String getAppVersionName(Context appContext) {
    PackageManager packageManager = appContext.getPackageManager();
    try {
      PackageInfo info = packageManager.getPackageInfo(appContext.getPackageName(), 0);
      return info.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      return null;
    }
  }

}
