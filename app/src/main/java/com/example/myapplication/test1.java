package com.example.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

    //辅助类建立时运行该方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE book ( book TEXT, book_url TEXT)";
        db.execSQL(sql);
        String sql1 = "CREATE TABLE content ( book TEXT PRIMARY KEY ASC, title TEXT,text TEXT,position char,paixu TEXT)";
        db.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}