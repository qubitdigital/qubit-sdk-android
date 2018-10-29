package com.qubit.android.sdk.internal.common.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.jetbrains.annotations.NotNull;

public class DatabaseInitializer {

  private static final int SCHEMA_VERSION = 1;

  private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
    @Override
    public Thread newThread(@NotNull Runnable runnable) {
      Thread thread = Executors.defaultThreadFactory().newThread(runnable);
      thread.setPriority(Thread.MIN_PRIORITY);
      return thread;
    }
  });
  private final Context appContext;
  private final TableInitializer[] tableInitializers;

  public DatabaseInitializer(Context appContext, TableInitializer ... tableInitializers) {
    this.appContext = appContext;
    this.tableInitializers = tableInitializers;
  }

  public Future<SQLiteDatabase> initDatabaseAsync() {
    return executorService.submit(new Callable<SQLiteDatabase>() {
      @Override
      public SQLiteDatabase call() throws Exception {
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, "qb-sdk", null, SCHEMA_VERSION) {
          @Override
          public void onCreate(SQLiteDatabase db) {
            for (TableInitializer tableInitializer : tableInitializers) {
              tableInitializer.onCreate(db);
            }
          }

          @Override
          public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (TableInitializer tableInitializer : tableInitializers) {
              tableInitializer.onUpgrade(db, oldVersion, newVersion);
            }
          }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        executorService.shutdown();
        return db;
      }
    });
  }

}
