package com.tzj.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tzj on 2018/5/21.
 */
public class SQLiteDelegate extends SQLiteOpenHelper {
    private static Context mAppCtx;
    public static void init(Context ctx){
        mAppCtx = ctx.getApplicationContext();
    }
    private ISqlite sqlite;

    public SQLiteDelegate(ISqlite iSqlite){
        super(mAppCtx, iSqlite.dbPath()+iSqlite.dbName()+".DB", null, iSqlite.version());
        this.sqlite = iSqlite;
        if (mAppCtx == null){
            throw new RuntimeException("请先调用"+getClass()+"里的 init 方法");
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        sqlite.initFields();
        super.onConfigure(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        sqlite.onCreate(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        sqlite.onUpgrade(db,oldVersion,newVersion);
    }

}
