package com.tzj.db.info;

import android.database.sqlite.SQLiteDatabase;

/**
 * Copyright © 2019 健康无忧网络科技有限公司<br>
 * Author:      唐泽金 tangzejin921@qq.com<br>
 * Version:     1.0.0<br>
 * Date:        2019/2/28 11:30<br>
 * Description: 空的更新
 */
public class DefaultDbinfo implements IDbinfo {

    @Override
    public String dbPath() {
        return "";
    }

    @Override
    public String dbName() {
        return getClass().getPackage().getName();
    }

    @Override
    public int version() {
        return 1;
    }

    @Override
    public String getKey() {
        return dbPath()+dbName();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
