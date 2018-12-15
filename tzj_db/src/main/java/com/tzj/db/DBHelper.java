package com.tzj.db;

import android.database.sqlite.SQLiteDatabase;


import com.tzj.db.annotations.FieldTag;
import com.tzj.db.annotations.FieldType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tzj on 2018/5/21.
 */
public class DBHelper extends BaseReflex implements ISqlite {
    @FieldTag(type = FieldType.ignore)
    protected static final HashMap<String, SQLiteDelegate> map = new HashMap<>();

    protected Date _id = new Date();//必定有一个_id 为插入时间

    public DBHelper() {
        if (getDbHelper() == null) {
            map.put(dbPath() + dbName(), new SQLiteDelegate(this));
        }
    }

    public DBHelper(Object obj) {
        //让子类 先new 后 new SQLiteDelegate
    }

    public SQLiteDelegate getDbHelper() {
        return map.get(dbPath() + dbName());
    }

    //=======================================================
    @Override
    public void initFields() {
        if (sqlFiles.get(getClass()) == null) {
            initField(getClass());
        }
    }

    @Override
    public int version() {
        return 1;
    }

    @Override
    public String dbPath() {
        return "";
    }

    @Override
    public String dbName() {
        return getClass().getPackage().getName();
    }

    @Override
    public String tabName() {
        return getClass().getSimpleName();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        map.put("_id","BIGINT primary key"/*+" autoincrement"*/);
        String tab = createSql(tabName(), sqlFiles.get(getClass()));
        db.execSQL(tab);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void close() {
        if (getDbHelper() != null) {
            getDbHelper().close();
            map.remove(dbPath() + dbName());
        }
    }

    protected String createSql(String tableName, List<SqlField> column) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("create table ")
                .append(tableName)
                .append("(");
        for (SqlField entry : column) {
            buffer.append(entry.getName()).append(" ").append(entry.getSqlType()).append(",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append(")");
        return buffer.toString();
    }

}