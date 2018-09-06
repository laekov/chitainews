package com.java.hejiaao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class History {
    static History instance;

    static public History getInstance(String baseDir) {
        if (instance == null) {
            instance = new History(baseDir);
        }
        return instance;
    }

    private SQLiteDatabase db;

    private History(String baseDir) {
        db = SQLiteDatabase.openOrCreateDatabase(baseDir + "/histories.db", null);
        this.checkTable("history");
        this.checkTable("like");
    }

    private void checkTable(String s) {
        db.execSQL("CREATE TABLE if not exists " + s + "(url text primary key)");
    }

    public void add(String table, String url) {
        try {
            ContentValues cval = new ContentValues();
            cval.put("url", url);
            db.insert(table, null, cval);
        } catch (Exception e) {
        }
    }

    public void del(String table, String url) {
        try {
            String [] args = {url};
            db.delete(table, "url=?", args);
        } catch (Exception e) {
        }
    }

    public boolean has(String table, String url) {
        String sel = "url=?";
        String selv [] = new String[1];
        selv[0] = url;
        Cursor cursor = db.query(table, null, sel, selv, null, null, null, null);
        return cursor.getCount() > 0;
    }
}
