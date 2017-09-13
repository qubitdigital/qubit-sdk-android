package com.qubit.android.sdk.internal.session.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.qubit.android.sdk.internal.session.model.SessionDataModel;

public class SessionRepositoryImpl implements SessionRepository {

  private static final String PREFERENCES_FILE = "qubit_session";
  private static final String CONFIGURATION_KEY = "session";

  private final Context appContext;
  private Gson gson;

  public SessionRepositoryImpl(Context appContext) {
    this.appContext = appContext;
  }

  @Override
  public void save(SessionDataModel sessionData) {
    SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    sharedPreferences.edit()
        .putString(CONFIGURATION_KEY, getGson().toJson(sessionData))
        .commit();
  }

  @Override
  public SessionDataModel load() {
    SharedPreferences sharedPref = appContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    String sessionDataJson = sharedPref.getString(CONFIGURATION_KEY, null);
    return sessionDataJson != null ? getGson().fromJson(sessionDataJson, SessionDataModel.class) : null;
  }

  private Gson getGson() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }

}
