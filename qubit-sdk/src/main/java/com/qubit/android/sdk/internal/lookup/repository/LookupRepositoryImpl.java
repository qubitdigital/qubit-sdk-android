package com.qubit.android.sdk.internal.lookup.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qubit.android.sdk.internal.logging.QBLogger;

public class LookupRepositoryImpl implements LookupRepository {

  private static final QBLogger LOGGER = QBLogger.getFor("LookupRepository");

  private static final String PREFERENCES_FILE = "qubit_lookup";
  private static final String LOOKUP_KEY = "lookup";

  private final Context appContext;
  private Gson gson;

  public LookupRepositoryImpl(Context appContext) {
    this.appContext = appContext;
  }

  @Override
  public void save(LookupCache lookupCache) {
    SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    sharedPreferences.edit()
        .putString(LOOKUP_KEY, getGson().toJson(lookupCache))
        .commit();
  }

  @Override
  public LookupCache load() {
    try {
      SharedPreferences sharedPref = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
      String lookupCacheJson = sharedPref.getString(LOOKUP_KEY, null);
      return lookupCacheJson != null ? getGson().fromJson(lookupCacheJson, LookupCache.class) : null;
    } catch (JsonSyntaxException e) {
      LOGGER.e("Error parsing lookup data JSON from local storage.", e);
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
