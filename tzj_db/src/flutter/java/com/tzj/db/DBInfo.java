package com.tzj.db;

import android.database.sqlite.SQLiteDatabase;

import com.tzj.db.info.DefaultDbinfo;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class DBInfo extends DefaultDbinfo {

    private String dbPath;
    private String dbName;
    private String tabName;
    private int version;
    /**
     *
     */
    private Map<String, Object> fields;

    private String where;
    private Object[] values;

    private String orderBy;
    private boolean desc;
    private int limit;
    private MethodChannel channel;

    public DBInfo(DBInfo dbInfo){
        this.dbPath = dbInfo.dbPath;
        this.dbName = dbInfo.dbName;
        this.tabName = dbInfo.tabName;
        this.version = dbInfo.version;
        this.fields = dbInfo.fields;
        this.fields = new HashMap<>(dbInfo.fields.size());
        Set<Map.Entry<String, Object>> entries = dbInfo.fields.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            this.fields.put(next.getKey(),next.getValue());
        }
        this.where = dbInfo.where;
        this.values = new Object[dbInfo.values.length];
        System.arraycopy(dbInfo.values,0,values,0,dbInfo.values.length);
        this.orderBy = dbInfo.orderBy;
        this.desc = dbInfo.desc;
        this.limit = dbInfo.limit;
        this.channel = dbInfo.channel;
    }

    public DBInfo(MethodCall methodCall, MethodChannel channel) {
        this.dbPath = methodCall.argument("dbPath");
        this.dbName = methodCall.argument("dbName");
        this.tabName = methodCall.argument("tabName");
        this.version = methodCall.argument("version");
        this.fields = methodCall.argument("fields");
        this.where = methodCall.argument("where");
        this.values = methodCall.argument("values");
        this.orderBy = methodCall.argument("orderBy");
        Object desc = methodCall.argument("desc");
        if (desc != null) {
            this.desc = (boolean) desc;
        }
        Object limit = methodCall.argument("limit");
        if (limit != null) {
            this.limit = (int) limit;
        }
        //将与当前时间相差 5分钟的改为Date型
        Iterator<Map.Entry<String, Object>> iterator = fields.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            if (next.getValue() instanceof Long) {
                Long value = (Long) next.getValue();
                if (Math.abs(value - System.currentTimeMillis()) <= 1000 * 60 * 5) {
                    fields.put(next.getKey(), new Date(value));
                }
            }
        }
        this.channel = channel;
    }

    public String getDbPath() {
        return dbPath;
    }

    public String getDbName() {
        return dbName;
    }

    public String getTabName() {
        return tabName;
    }

    public int getVersion() {
        return version;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public boolean isDesc() {
        return desc;
    }

    public int getLimit() {
        return limit;
    }

    public String getWhere() {
        return where;
    }

    public Object[] getValues() {
        return values;
    }

    public Map<String, Object> getInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("dbPath", dbPath);
        map.put("dbName", dbName);
        map.put("tabName", tabName);
        map.put("version", version);
        return map;
    }


    @Override
    public String dbPath() {
        return dbPath;
    }

    @Override
    public String dbName() {
        return dbName;
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        Map<String, Object> map = new HashMap<>();
        map.put("dbPath", dbPath());
        map.put("dbName", dbName());
        map.put("oldVersion", oldVersion);
        map.put("newVersion", newVersion);
        channel.invokeMethod("onUpgrade", map, new MethodChannel.Result() {
            @Override
            public void success(Object o) {
                String sql = (String) o;
                db.execSQL(sql);
            }

            @Override
            public void error(String s, String s1, Object o) {
                //TODO
            }

            @Override
            public void notImplemented() {
                //TODO
            }
        });
    }
}
