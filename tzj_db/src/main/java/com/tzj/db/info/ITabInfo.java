package com.tzj.db.info;


import android.database.sqlite.SQLiteDatabase;

/**
 * Created by tzj on 2018/5/21.
 */

public interface ITabInfo {

    /**
     * @return 是否已经初始化过了
     */
    boolean initFields();

    String tabName();

    void onCreate(SQLiteDatabase db);

    /**
     * 任何一个表调用将关闭数据库
     */
    void close();

    IDbinfo upgrade();

}
