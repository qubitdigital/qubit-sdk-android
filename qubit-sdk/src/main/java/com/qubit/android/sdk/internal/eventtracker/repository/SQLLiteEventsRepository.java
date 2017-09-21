package com.qubit.android.sdk.internal.eventtracker.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import com.qubit.android.sdk.internal.common.repository.TableInitializer;
import com.qubit.android.sdk.internal.logging.QBLogger;
import com.qubit.android.sdk.internal.session.SessionService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.qubit.android.sdk.internal.common.repository.SQLUtil.*;

public class SQLLiteEventsRepository implements EventsRepository {

  private static final QBLogger LOGGER = QBLogger.getFor("SQLLiteEventsRepository");
  private static final String DATABASE_INITIALIZATION_ERROR = "Database initialization error";

  private static final String TABLE_NAME = "EVENT";
  private static final String WAS_TRIED_TO_SEND_COLUMN = "WAS_TRIED_TO_SEND";
  private static final String[] ALL_COLUMNS =
      { "_id", "GLOBAL_ID", "TYPE", "EVENT_BODY", WAS_TRIED_TO_SEND_COLUMN, "CREATION_TIMESTAMP",
      "CONTEXT_VIEW_NUMBER", "CONTEXT_SESSION_NUMBER", "CONTEXT_SESSION_VIEW_NUMBER",
      "CONTEXT_VIEW_TIMESTAMP", "CONTEXT_SESSION_TIMESTAMP", "SEQ"};

  private final Future<SQLiteDatabase> databaseFuture;
  private SQLiteDatabase database;
  private SQLiteStatement insertStatement;
  private String selectFirstSql;
  private SQLiteStatement selectCountStatement;
  private SQLiteStatement deleteOneStatement;
  private SQLiteStatement updateWasTriedToSendOneStatement;

  public SQLLiteEventsRepository(Future<SQLiteDatabase> databaseFuture) {
    this.databaseFuture = databaseFuture;
  }

  public static TableInitializer tableInitializer() {
    return new EventsTableInitializer();
  }

  @Override
  public boolean init() {
    try {
      LOGGER.d("init()");
      database = databaseFuture.get();
      insertStatement = database.compileStatement(createSqlInsert(TABLE_NAME, ALL_COLUMNS));
      selectFirstSql = createSqlSelect(TABLE_NAME, ALL_COLUMNS, "_id ASC", "?");
      selectCountStatement = database.compileStatement("SELECT COUNT(*) FROM " + TABLE_NAME);
      deleteOneStatement = database.compileStatement("DELETE FROM " + TABLE_NAME + " WHERE _id = ?");
      updateWasTriedToSendOneStatement = database.compileStatement(
          "UPDATE " + TABLE_NAME + " SET WAS_TRIED_TO_SEND = 1 WHERE _id = ?");

      LOGGER.d("EventRepository initialized");
      return true;
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.e(DATABASE_INITIALIZATION_ERROR, e);
      return false;
    }
  }

  @Override
  public EventModel insert(EventModel newEvent) {
    LOGGER.d("insert");
    bindValues(insertStatement, newEvent);
    long id = insertStatement.executeInsert();
    newEvent.setId(id);
    return newEvent;
  }

  @Override
  public EventModel selectFirst() {
    LOGGER.d("selectFirst(1)");
    Cursor cursor = database.rawQuery(selectFirstSql, new String[] {"1"});
    try {
      return cursor.moveToNext() ? readEntity(cursor) : null;
    } finally {
      cursor.close();
    }
  }

  @Override
  public List<EventModel> selectFirst(int number) {
    LOGGER.d("selectFirst(N)");
    Cursor cursor = database.rawQuery(selectFirstSql, new String[] { Integer.toString(number) });
    try {
      ArrayList<EventModel> events = new ArrayList<>(number);
      while (cursor.moveToNext()) {
        events.add(readEntity(cursor));
      }
      return events;
    } finally {
      cursor.close();
    }
  }

  @Override
  public boolean delete(long id) {
    LOGGER.d("delete(1)");
    deleteOneStatement.bindLong(0, id);
    int rowsAffected = deleteOneStatement.executeUpdateDelete();
    return rowsAffected > 0;
  }

  @Override
  public int delete(Collection<Long> ids) {
    LOGGER.d("delete(N)");
    return database.delete(TABLE_NAME, getWhereIdInClause(ids.size()), toSqlArgs(ids));
  }

  @Override
  public int deleteOlderThan(long timeMs) {
    LOGGER.d("deleteOlderThan");
    return database.delete(TABLE_NAME, "CREATION_TIMESTAMP < ? AND TYPE <> ?",
        new String[] { Long.toString(timeMs), SessionService.SESSION_EVENT_TYPE});
  }

  @Override
  public boolean updateSetWasTriedToSend(long id) {
    LOGGER.d("updateSetWasTriedToSend(1)");
    updateWasTriedToSendOneStatement.bindLong(0, id);
    int rowsAffected = updateWasTriedToSendOneStatement.executeUpdateDelete();
    return rowsAffected > 0;
  }

  @Override
  public int updateSetWasTriedToSend(Collection<Long> ids) {
    LOGGER.d("updateSetWasTriedToSend(N)");
    ContentValues contentValues = new ContentValues(1);
    contentValues.put(WAS_TRIED_TO_SEND_COLUMN, 1L);
    return database.update(TABLE_NAME, contentValues, getWhereIdInClause(ids.size()), toSqlArgs(ids));
  }

  @Override
  public int count() {
    LOGGER.d("count");
    return (int) selectCountStatement.simpleQueryForLong();
  }


  private static class EventsTableInitializer implements TableInitializer {
    @Override
    public void onCreate(SQLiteDatabase db) {
      LOGGER.i("Creating table for events");
      createTable(db, false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      LOGGER.i("Upgrading events schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
      dropTable(db, true);
      onCreate(db);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
      String constraint = ifNotExists ? "IF NOT EXISTS " : "";
      db.execSQL("CREATE TABLE " + constraint + TABLE_NAME + " (" //
          + "_id INTEGER PRIMARY KEY AUTOINCREMENT ," // 0: id
          + "GLOBAL_ID TEXT," // 1: globalId
          + "TYPE TEXT NOT NULL ," // 2: type
          + "EVENT_BODY TEXT NOT NULL ," // 3: eventBody
          + "WAS_TRIED_TO_SEND INTEGER NOT NULL ," // 4: wasTriedToSend
          + "CREATION_TIMESTAMP INTEGER NOT NULL ," // 5: creationTimestamp
          + "CONTEXT_VIEW_NUMBER INTEGER NULL ," // 6: contextViewNumber
          + "CONTEXT_SESSION_NUMBER INTEGER NULL ," // 7: contextSessionNumber
          + "CONTEXT_SESSION_VIEW_NUMBER INTEGER NULL ," // 8: contextSessionViewNumber
          + "CONTEXT_VIEW_TIMESTAMP INTEGER NULL ," // 9: contextViewTs
          + "CONTEXT_SESSION_TIMESTAMP INTEGER NULL, " // 10: contextSessionTs
          + "SEQ INTEGER NOT NULL " // 11: seq
          + ");"
      );
      // Add Indexes
      db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_EVENT_GLOBAL_ID ON " + TABLE_NAME + " (GLOBAL_ID ASC);");
      db.execSQL("CREATE INDEX " + constraint + "IDX_CREATION_TIMESTAMP ON "
          + TABLE_NAME + " (CREATION_TIMESTAMP ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
      String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + TABLE_NAME;
      db.execSQL(sql);
    }

  }

  @NonNull
  private static String getWhereIdInClause(int count) {
    return "_id in " + createInParametersSet(count);
  }


  @SuppressWarnings("checkstyle:magicnumber")
  private static void bindValues(SQLiteStatement stmt, EventModel entity) {
    stmt.clearBindings();

    bindNullableLong(stmt, 1, entity.getId());
    bindNullableString(stmt, 2, entity.getGlobalId());
    stmt.bindString(3, entity.getType());
    stmt.bindString(4, entity.getEventBody());
    stmt.bindLong(5, entity.getWasTriedToSend() ? 1L : 0L);
    bindNullableLong(stmt, 6, entity.getCreationTimestamp());
    bindNullableLong(stmt, 7, entity.getContextViewNumber());
    bindNullableLong(stmt, 8, entity.getContextSessionNumber());
    bindNullableLong(stmt, 9, entity.getContextSessionViewNumber());
    bindNullableLong(stmt, 10, entity.getContextViewTimestamp());
    bindNullableLong(stmt, 11, entity.getContextSessionTimestamp());
    stmt.bindLong(12, entity.getSeq());
  }

  private static EventModel readEntity(Cursor cursor) {
    EventModel entity = new EventModel();
    readEntity(cursor, entity);
    return entity;
  }

  @SuppressWarnings("checkstyle:magicnumber")
  private static void readEntity(Cursor cursor, EventModel entity) {
    entity.setId(getNullableLong(cursor, 0));
    entity.setGlobalId(cursor.isNull(1) ? null : cursor.getString(1));
    entity.setType(cursor.getString(2));
    entity.setEventBody(cursor.getString(3));
    entity.setWasTriedToSend(cursor.getShort(4) != 0);
    entity.setCreationTimestamp(cursor.getLong(5));
    entity.setContextViewNumber(getNullableLong(cursor, 6));
    entity.setContextSessionNumber(getNullableLong(cursor, 7));
    entity.setContextSessionViewNumber(getNullableLong(cursor, 8));
    entity.setContextViewTimestamp(getNullableLong(cursor, 9));
    entity.setContextSessionTimestamp(getNullableLong(cursor, 10));
    entity.setSeq(cursor.getLong(11));
  }

}
