package com.tzj.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tzj.db.info.IDbinfo;
import com.tzj.db.info.ITabInfo;

/**
 * Created by tzj on 2018/5/21.
 * 每张表会产生一个实例
 */
public class SQLiteDelegate extends SQLiteOpenHelper {
    private static Context mAppCtx;
    public static void init(Context ctx){
        mAppCtx = ctx.getApplicationContext();
    }

//    private ITabInfo tabInfo;
    private IDbinfo upgrade;

    public SQLiteDelegate(ITabInfo tabInfo, IDbinfo iUpgrade){
        super(mAppCtx, iUpgrade.getKey()+".DB", null, iUpgrade.version());
//        this.tabInfo = tabInfo;
        this.upgrade = iUpgrade;
        if (mAppCtx == null){
            throw new RuntimeException("请先调用"+getClass()+"里的 init 方法");
        }
    }

    /**
     * 代替 onConfigure onCreate
     */
    public void onTab(ITabInfo tab){
        if (!tab.initFields()){
            SQLiteDatabase db = getWritableDatabase();
            if (!isExist(db,tab.tabName())){
                tab.onCreate(db);
            }
        }
    }

//    @Override
//    public void onConfigure(SQLiteDatabase db) {
//        tabInfo.initFields();
//        super.onConfigure(db);
//        onCreate(db);
//    }
    @Deprecated
    @Override
    public void onCreate(SQLiteDatabase db) {
        //被 onTab 代替了
//        //这里可能会被调两次
//        if (isExist(db,tabInfo.tabName())){
//            tabInfo.onCreate(db);
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgrade.onUpgrade(db,oldVersion,newVersion);
    }

    /**
     * 是否第一次 或者 已经创建了表
     */
    private boolean isExist(SQLiteDatabase db,String tableName){
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT count(name) FROM sqlite_master where name=?",new String[]{tableName});
        int columnIndex = cursor.getColumnIndex("count(name)");
        int count = 0;
        if (cursor.moveToNext()){
            count = cursor.getInt(columnIndex);
        }
        cursor.close();
        db.endTransaction();
        return count > 0;
    }

}
