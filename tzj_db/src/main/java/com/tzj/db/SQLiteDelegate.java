package com.tzj.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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
        if (!isExist(db,sqlite.tabName())){
            onCreate(db);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        sqlite.onCreate(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        sqlite.onUpgrade(db,oldVersion,newVersion);
    }

    /**
     * 是否第一次 或者 已经创建了表
     */
    private boolean isExist(SQLiteDatabase db,String tableName){
        db.beginTransaction();
        List<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master where type='table'",new String[]{});
        while (cursor.moveToNext()){
            String temp = cursor.getString(0);
            list.add(temp);
        }
        list.remove("sqlite_sequence");
        list.remove("android_metadata");
        cursor.close();
        db.endTransaction();
        //第一次 或者 已经创建了表
        if (list.size() == 0 || list.contains(tableName)){
            return true;
        }else{
            return false;
        }
    }

}
