package com.tzj.db;


import android.database.sqlite.SQLiteDatabase;

/**
 * Created by tzj on 2018/5/21.
 */

public interface ISqlite {

    void initFields();
    int version();
    String dbPath();
    String dbName();

    String tabName();

    void onCreate(SQLiteDatabase db);
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    void close();

}
