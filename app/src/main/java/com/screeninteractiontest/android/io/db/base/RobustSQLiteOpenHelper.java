package com.screeninteractiontest.android.io.db.base;
/**
 * From ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public abstract class RobustSQLiteOpenHelper extends SQLiteOpenHelper {
    private static List<String> mTableNames = new LinkedList<>();

    public RobustSQLiteOpenHelper(final Context context, final String name,
                                  final CursorFactory factory, final Integer version) {
        super(context, name, factory, version);
    }

    protected static void addTableName(final String tableName) {
        mTableNames.add(tableName);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        dropAllTables(db);
    }

    @Override
    public final void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        try {
            onRobustUpgrade(db, oldVersion, newVersion);
        } catch (SQLiteException e) {
            // The database has entered an unknown state. Try to recover.
            try {
                regenerateTables(db);
            } catch (SQLiteException e2) {
                dropAndCreateTables(db);
            }
        }
    }

    public abstract void onRobustUpgrade(final SQLiteDatabase db, final int oldVersion,
                                         final int newVersion) throws SQLiteException;

    private void regenerateTables(final SQLiteDatabase db) {
        dropAllTablesWithPrefix(db, "OLD_");

        for (String tableName : mTableNames)
            db.execSQL("ALTER TABLE " + tableName + " RENAME TO OLD_"
                    + tableName);

        onCreate(db);

        for (String tableName : mTableNames)
            repopulateTable(db, tableName);

        dropAllTablesWithPrefix(db, "OLD_");
    }

    private void repopulateTable(final SQLiteDatabase db, final String tableName) {
        String columns = getTableColumnNames(db, tableName);

        String sql = "INSERT INTO " + tableName + " (" + columns + ") SELECT " + columns + " FROM" +
                " OLD_" + tableName;
        db.execSQL(sql);
    }

    private String getTableColumnNames(final SQLiteDatabase db, final String tableName) {
        StringBuilder sb = new StringBuilder();

        Cursor fields = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        while (fields.moveToNext()) {
            if (!fields.isFirst())
                sb.append(", ");
            sb.append(fields.getString(1));
        }
        fields.close();

        return sb.toString();
    }

    private void dropAndCreateTables(final SQLiteDatabase db) {
        dropAllTables(db);
        onCreate(db);
    }

    private void dropAllTablesWithPrefix(final SQLiteDatabase db, final String prefix) {
        List<String> deletedTables = new LinkedList<>();
        for (String tableName : mTableNames) {
            db.execSQL("DROP TABLE IF EXISTS " + prefix + tableName);
            deletedTables.add(prefix + tableName);
        }
        for (String tableName : deletedTables)
            mTableNames.remove(tableName);
    }

    protected void dropAllTables(final SQLiteDatabase db) {
        dropAllTablesWithPrefix(db, "");
    }
}

