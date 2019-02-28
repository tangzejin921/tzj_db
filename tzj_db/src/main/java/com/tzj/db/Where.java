package com.tzj.db;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


import com.tzj.db.callback.ICallBack;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by tzj on 2018/5/28.
 */
public class Where {
    public static Handler dbHandler;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    static {
        if(dbHandler==null){
            HandlerThread dbHandlerThread = new HandlerThread("dbHandlerThread");
            dbHandlerThread.start();
            dbHandler = new Handler(dbHandlerThread.getLooper());
        }
    }

    private Handler mHandler = uiHandler;
    private WeakReference<BaseDB> weakReference;
    private String where="";
    private Object[] data;

    public Where(BaseDB baseDB, String where, Object... data) {
        weakReference = new WeakReference<BaseDB>(baseDB);
        this.where = where;
        this.data = data;
    }

    public Where handler(Handler handler){
        mHandler = handler;
        return this;
    }

    public Where orderBy(String name){
        if (where == null){
            where = "";
        }
        if (name!=null){
            where = where+" order by "+name;
        }
        return this;
    }

    public Where desc(boolean b){
        if (where == null){
            where = "";
        }
        if (b){
            where = where+" desc";
        }
        return this;
    }

    public Where limit(int count){
        if (where == null){
            where = "";
        }
        if (count > 0){
            where = where+" limit "+count;
        }
        return this;
    }

    public void insert(final ICallBack callback){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseDB baseDB = weakReference.get();
                if(baseDB!=null){
                    final long insert = baseDB.insert(where,data);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.result(null, insert);
                        }
                    });
                }
            }
        };
        dbHandler.post(runnable);
    }
    public void delete(final ICallBack callback){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseDB baseDB = weakReference.get();
                if(baseDB!=null){
                    final int delete = baseDB.delete(where, toStrs(data));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.result(null,delete);
                        }
                    });
                }
            }
        };
        dbHandler.post(runnable);
    }
    public void select(final ICallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseDB baseDB = weakReference.get();
                if(baseDB!=null){
                    final List select = baseDB.select(where, toStrs(data));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.result(select,select.size());
                        }
                    });
                }
            }
        };
        dbHandler.post(runnable);
    }
    public void update(final ICallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseDB baseDB = weakReference.get();
                if(baseDB!=null){
                    final int update = baseDB.update(where, toStrs(data));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.result(null,update);
                        }
                    });
                }
            }
        };
        dbHandler.post(runnable);
    }
    public void count(final ICallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseDB baseDB = weakReference.get();
                if(baseDB!=null){
                    final int count = baseDB.count(where, toStrs(data));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.result(null,count);
                        }
                    });
                }
            }
        };
        dbHandler.post(runnable);
    }
    private String[] toStrs(Object[] objs){
        if (objs == null || objs.length == 0){
            return null;
        }
        String[] strs = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            strs[i] = objs[i].toString();
        }
        return strs;
    }

}
