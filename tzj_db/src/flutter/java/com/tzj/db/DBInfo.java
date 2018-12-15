package com.tzj.db;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;

public class DBInfo {

    private String dbPath;
    private String dbName;
    private String tabName;
    private int version;
    private Map<String, Object> fields;

    private String where;
    private Object[] values;

    private String orderBy;
    private boolean desc;
    private int limit;


    public DBInfo(MethodCall methodCall) {
        dbPath = methodCall.argument("dbPath");
        dbName = methodCall.argument("dbName");
        tabName = methodCall.argument("tabName");
        version = methodCall.argument("version");
        fields = methodCall.argument("fields");
        where = methodCall.argument("where");
        values = methodCall.argument("values");
        orderBy = methodCall.argument("orderBy");
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

    public String getKey() {
        return getDbPath() + getDbName();
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

}
