package com.qubit.android.sdk.internal.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.google.gson.Gson;

public class ConfigurationRepositoryImpl implements ConfigurationRepository {

  private static final String PREFERENCES_FILE = "PREFERENCES_FILE";
  private static final String CONFIGURATION_KEY = "CONFIGURATION_KEY";

  private final Context appContext;
  private final Gson gson;

  public ConfigurationRepositoryImpl(Context context, Gson gson) {
    this.appContext = context;
    this.gson = gson;
  }

  @Override
  public void save(ConfigurationModel configuration) {
    SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    sharedPreferences.edit()
        .putString(CONFIGURATION_KEY, gson.toJson(configuration))
        .apply();
  }

  @Override
  @Nullable
  public ConfigurationModel load() {
    SharedPreferences sharedPref = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    String configurationJson = sharedPref.getString(CONFIGURATION_KEY, null);
    return configurationJson != null ? gson.fromJson(configurationJson, ConfigurationModel.class) : null;
  }
}
