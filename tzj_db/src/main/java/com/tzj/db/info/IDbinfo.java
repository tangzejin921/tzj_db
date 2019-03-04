package com.tzj.db.info;

import android.database.sqlite.SQLiteDatabase;

/**
 * Copyright © 2019 健康无忧网络科技有限公司<br>
 * Author:      唐泽金 tangzejin921@qq.com<br>
 * Version:     1.0.0<br>
 * Date:        2019/2/28 11:20<br>
 * Description: 数据库更新接口
 */
public interface IDbinfo {

    String dbPath();
    String dbName();
    int version();
    String getKey();

    /**
     * 新建的表可以不用处理
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
