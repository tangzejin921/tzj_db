package com.tzj.db;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.tzj.db.info.IDbinfo;

import java.util.ArrayList;
import java.util.Date;
import android.content.ContentValues;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FlutterDB extends BaseDB {

    public void setDbInfo(DBInfo dbInfo) {
        this.dbinfo = dbInfo;
    }

    public DBInfo getDbInfo() {
        return (DBInfo) dbinfo;
    }


    /**
     * copy 作为实体类用
     */
    private FlutterDB(FlutterDB flutterDB) {
        super(null);
        this.dbinfo = new DBInfo(flutterDB.getDbInfo());
    }

    public FlutterDB(Object obj) {
        super(null);
        dbinfo = (DBInfo) obj;
        SQLiteDelegate dbHelper = getDbHelper();
        if (dbHelper == null) {
            map.put(dbinfo.getKey(), dbHelper = new SQLiteDelegate(this, dbinfo));
        }
        dbHelper.onTab(this);
    }

    public Where where() {
        return super.where(getDbInfo().getWhere(), getDbInfo().getValues())
                .orderBy(getDbInfo().getOrderBy())
                .desc(getDbInfo().isDesc())
                .limit(getDbInfo().getLimit());
    }

    @Override
    protected ContentValues toValue(Class<?> c) {
        ContentValues map = new ContentValues();
        List<SqlField> sqlFields = sqlFiles.get(dbinfo.getKey() + tabName());
        for (SqlField sf : sqlFields) {
            try {
                Object value = getDbInfo().getFields().get(sf.getName());
                if (value instanceof Date) {
                    value = ((Date) value).getTime();
                }
                addToContentValue(map,sf.getName(),value);
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
    public String tabName() {
        return getDbInfo().getTabName();
    }

    @Override
    public boolean initFields() {
        if (sqlFiles.get(dbinfo.getKey() + tabName()) == null) {
            initField(getClass());
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tab = createSql(tabName(), sqlFiles.get(dbinfo.getKey() + tabName()));
        db.execSQL(tab);
    }


    @Override
    public IDbinfo upgrade() {
        //这里没意义
        return dbinfo;
    }

    //=========================================

    /**
     * 初始化这个类
     */
    @Override
    protected List<SqlField> initField(Class<?> c) {
        List<SqlField> sqlFieldList = sqlFiles.get(dbinfo.getKey() + tabName());
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
            Iterator<Map.Entry<String, Object>> iterator = getDbInfo().getFields().entrySet().iterator();
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
            sqlFiles.put(dbinfo.getKey() + tabName(), sqlFieldList);
        }
        return sqlFieldList;
    }

    @Override
    protected void filling(Object obj, Class c, Cursor cursor) {
        if (!(obj instanceof FlutterDB)) {
            return;
        }
        FlutterDB flutterDB = (FlutterDB) obj;
        List<SqlField> sqlFields = sqlFiles.get(dbinfo.getKey() + tabName());
        for (SqlField sf : sqlFields) {
            try {
                Object value = sf.getCursorValue(cursor);
                if (value instanceof Date){
                    value = ((Date)value).getTime();
                }
                flutterDB.getDbInfo().getFields().put(sf.getName(), value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
