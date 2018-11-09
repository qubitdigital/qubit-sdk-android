package com.qubit.android.sdk.internal.configuration.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qubit.android.sdk.internal.common.logging.QBLogger;

import org.jetbrains.annotations.Nullable;

public class ConfigurationRepositoryImpl implements ConfigurationRepository {

  private static final QBLogger LOGGER = QBLogger.getFor("ConfigurationRepository");

  private static final String PREFERENCES_FILE = "qubit_configuration";
  private static final String CONFIGURATION_KEY = "configuration";

  private final Context appContext;
  private Gson gson;

  public ConfigurationRepositoryImpl(Context context) {
    this.appContext = context;
  }

  @Override
  public void save(ConfigurationModel configuration) {
    SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    sharedPreferences.edit()
        .putString(CONFIGURATION_KEY, getGson().toJson(configuration))
        .commit();
  }

  @Override
  @Nullable
  public ConfigurationModel load() {
    try {
      SharedPreferences sharedPref = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
      String configurationJson = sharedPref.getString(CONFIGURATION_KEY, null);
      return configurationJson != null ? getGson().fromJson(configurationJson, ConfigurationModel.class) : null;
    } catch (JsonSyntaxException e) {
      LOGGER.e("Error parsing configuration JSON from local storage.", e);
      return null;
    }
  }

  private Gson getGson() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }
}
