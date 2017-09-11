package com.qubit.android.sdk.internal.common.repository;

import android.database.sqlite.SQLiteDatabase;

public interface TableInitializer {
  void onCreate(SQLiteDatabase db);
  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
