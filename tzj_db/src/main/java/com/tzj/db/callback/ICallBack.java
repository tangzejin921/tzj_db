package com.tzj.db.callback;

import com.tzj.db.BaseDB;

import java.util.List;

/**
 * Created by tzj on 2018/5/28.
 */

public interface ICallBack<T extends BaseDB> {
    void result(List<T> list, long idRoNum);
    void err(Exception e, long code);
}
