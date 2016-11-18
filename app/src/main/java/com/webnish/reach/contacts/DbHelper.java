package com.webnish.reach.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shashidhar on 25/5/16.
 */
public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context,"contacts.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query="CREATE TABLE lib_user (user_id INTEGER PRIMARY KEY,name TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS data");
        onCreate(db);
    }

    public boolean lib_user(String user_id, String name) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("user_id", user_id);
    values.put("name", name);
    long result = db.insert("lib_user", null, values);
    if (result == -1)
            return false;
    else
        return true;
}

    public String get_lib_user_id(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        String user_id = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT * FROM lib_user ";
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                user_id = cursor.getString(cursor.getColumnIndex("user_id"));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return user_id;
    }
}
