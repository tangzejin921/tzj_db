package com.tzj.db;

import java.util.List;

/**
 * Created by tzj on 2018/5/28.
 */
public abstract class DBCallBack<T extends BaseDB> implements ICallBack<T>{
    public abstract void onResult(List<T> list, long idRoNum);
    @Override
    public void result(List<T> list, long idRoNum) {
        onResult(list,idRoNum);
    }
    @Override
    public void err(Exception e, long code) {

    }
}
