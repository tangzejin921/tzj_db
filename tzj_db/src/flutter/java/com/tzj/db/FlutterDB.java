package com.tzj.db;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.tzj.db.annotations.FieldTag;
import com.tzj.db.annotations.FieldType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

public class FlutterDB extends BaseDB {

    @FieldTag(type = FieldType.ignore)
    private DBInfo dbInfo;

    public void setDbInfo(DBInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    public DBInfo getDbInfo() {
        return dbInfo;
    }

    @FieldTag(type = FieldType.ignore)
    private MethodChannel channel;

    private FlutterDB(FlutterDB flutterDB) {
        super(null);
        this.dbInfo = flutterDB.dbInfo;
    }

    public FlutterDB(Object obj, MethodChannel channel) {
        super(obj);
        this.channel = channel;
        dbInfo = (DBInfo) obj;
        if (getDbHelper() == null) {
            map.put(dbPath() + dbName(), new SQLiteDelegate(this));
        }
    }

    public Where where() {
        return super.where(dbInfo.getWhere(), dbInfo.getValues())
                .orderBy(dbInfo.getOrderBy())
                .desc(dbInfo.isDesc())
                .limit(dbInfo.getLimit());
    }

    @Override
    protected Map<String, Object> toValue(Class<?> c) {
        Map<String, Object> map = new HashMap<>();
        List<SqlField> sqlFields = sqlFiles.get(dbPath() + dbName() + tabName());
        for (SqlField sf : sqlFields) {
            try {
                Object value = dbInfo.getFields().get(sf.getName());
                if (value instanceof Date) {
                    value = ((Date) value).getTime();
                }
                map.put(sf.getName(), value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    protected <T extends BaseDB> List<T> toList(Cursor cursor) {
        List ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            try {
                FlutterDB flutterDB = new FlutterDB(this);
                filling(flutterDB, getClass(), cursor);
                ret.add(flutterDB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return ret;
    }

    //=========================================
    @Override
    public String dbPath() {
        return dbInfo.getDbPath();
    }

    @Override
    public String dbName() {
        return dbInfo.getDbName();
    }

    @Override
    public String tabName() {
        return dbInfo.getTabName();
    }

    @Override
    public int version() {
        return dbInfo.getVersion();
    }

    @Override
    public void initFields() {
        if (sqlFiles.get(dbPath() + dbName() + tabName()) == null) {
            initField(getClass());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tab = createSql(tabName(), sqlFiles.get(dbPath() + dbName() + tabName()));
        db.execSQL(tab);
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
    //=========================================

    /**
     * 初始化这个类
     */
    protected List<SqlField> initField(Class<?> c) {
        List<SqlField> sqlFieldList = sqlFiles.get(dbPath() + dbName() + tabName());
        if (sqlFieldList == null) {
            sqlFieldList = new ArrayList<>();
            //id
            SqlField id = new SqlField();
            id.setName("_id");
            id.setInitValue(System.currentTimeMillis());
            id.setSqlType("datetime default (now())");
            id.setType(Date.class);
            sqlFieldList.add(id);
            //flutter 传过来的
            Iterator<Map.Entry<String, Object>> iterator = dbInfo.getFields().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                String name = next.getKey();
                Object initValue = next.getValue();//不要为空
                if (initValue == null) {
                    throw new RuntimeException("请给 " + name + " 默认值");
                }
                String sqlType = initValue.getClass().getSimpleName();
                Class type = initValue.getClass();

                if (initValue instanceof String) {
                    sqlType = "text";
                    if (TextUtils.isEmpty((String)initValue)){
                        initValue = "''";
                    }
                } else if (initValue instanceof Date) {
                    sqlType = "datetime";
                }
                if (initValue instanceof Date) {
                    initValue = "(now())";
                }
                sqlType += " default " + initValue;

                SqlField sqlField = new SqlField();
                sqlField.setName(name);
                sqlField.setInitValue(initValue);
                sqlField.setSqlType(sqlType);
                sqlField.setType(type);
                sqlFieldList.add(sqlField);
            }
            sqlFiles.put(dbPath() + dbName() + tabName(), sqlFieldList);
        }
        return sqlFieldList;
    }

    @Override
    protected void filling(Object obj, Class c, Cursor cursor) {
        if (!(obj instanceof FlutterDB)) {
            return;
        }
        FlutterDB flutterDB = (FlutterDB) obj;
        List<SqlField> sqlFields = sqlFiles.get(dbPath() + dbName() + tabName());
        for (SqlField sf : sqlFields) {
            try {
                Object value = sf.getCursorValue(cursor);
                if (value instanceof Date){
                    value = ((Date)value).getTime();
                }
                flutterDB.dbInfo.getFields().put(sf.getName(), value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
