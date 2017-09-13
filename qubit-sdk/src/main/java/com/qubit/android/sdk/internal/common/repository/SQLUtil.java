package com.qubit.android.sdk.internal.common.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import java.util.Collection;

public final class SQLUtil {

  private SQLUtil() {
  }

  public static String createSqlInsert(String tableName, String[] columns) {
    StringBuilder builder = new StringBuilder("INSERT INTO ");
    builder.append('"').append(tableName).append('"').append(" (");
    appendColumns(builder, columns);
    builder.append(") VALUES (");
    appendPlaceholders(builder, columns.length);
    builder.append(')');
    return builder.toString();
  }

  public static String createSqlSelect(String tableName, String[] columns, String orderBy, String limit) {
    StringBuilder builder = new StringBuilder("SELECT ");
    appendColumns(builder, columns);
    builder.append(" FROM ").append(tableName);
    if (orderBy != null) {
      builder.append(" ORDER BY " + orderBy);
    }
    if (limit != null) {
      builder.append(" LIMIT " + limit);
    }
    return builder.toString();
  }

  public static String createInParametersSet(long count) {
    StringBuilder builder = new StringBuilder("(");
    for (int i = 0; i < count; i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append("?");
    }
    builder.append(")");
    return builder.toString();
  }

  public static String[] toSqlArgs(Collection<Long> ids) {
    String [] args = new String[ids.size()];
    int i = 0;
    for (Long id : ids) {
      args[i++] = id.toString();
    }
    return args;
  }

  private static StringBuilder appendColumns(StringBuilder builder, String[] columns) {
    int length = columns.length;
    for (int i = 0; i < length; i++) {
      builder.append('"').append(columns[i]).append('"');
      if (i < length - 1) {
        builder.append(',');
      }
    }
    return builder;
  }

  private static StringBuilder appendPlaceholders(StringBuilder builder, int count) {
    for (int i = 0; i < count; i++) {
      if (i < count - 1) {
        builder.append("?,");
      } else {
        builder.append('?');
      }
    }
    return builder;
  }

  public static Long getNullableLong(Cursor cursor, int i) {
    return cursor.isNull(i) ? null : cursor.getLong(i);
  }

  public static void bindNullableLong(SQLiteStatement stmt, int index, Long value) {
    if (value != null) {
      stmt.bindLong(index, value);
    }
  }

  public static void bindNullableString(SQLiteStatement stmt, int index, String value) {
    if (value != null) {
      stmt.bindString(index, value);
    }
  }
}
