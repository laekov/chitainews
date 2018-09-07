package com.java.hejiaao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.java.hejiaao.FetchXML.DataItem;

import java.util.ArrayList;

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
        this.checkBigTable("newscache");
    }

    private void checkTable(String s) {
        db.execSQL("CREATE TABLE if not exists " + s + "(url text primary key)");
    }

    private void checkBigTable(String s) {
        db.execSQL("CREATE TABLE if not exists " + s + "(url text primary key, title text, content text)");
    }

    public void add(String table, String url) {
        try {
            ContentValues cval = new ContentValues();
            cval.put("url", url);
            db.insert(table, null, cval);
        } catch (Exception e) {
        }
    }

    public void addCache(String url, String title, String content) {
        try {
            ContentValues cval = new ContentValues();
            cval.put("url", url);
            cval.put("title", title);
            cval.put("content", content);
            db.insert("newscache", null, cval);
        } catch (Exception e) {
        }
    }

    public DataItem getCache(String url) {
        String selv [] = {url};
        Cursor cursor = db.query("newscache", null, "url=?", selv, null, null, null, null);
        if (cursor.getCount() == 0) {
            return new DataItem("news not found", "", "");
        }
        cursor.moveToFirst();
        return new DataItem(cursor.getString(1), cursor.getString(0), cursor.getString(2));
    }

    public ArrayList<String> getStars() {
        ArrayList<String> res = new ArrayList();
        String [] columns = {"url"};
        Cursor cursor = db.query("like", columns, null, null, null, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); ++ i) {
            String url = cursor.getString(0);
            res.add(url);
            cursor.moveToNext();
        }
        return res;
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
