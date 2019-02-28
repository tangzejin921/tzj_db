package com.tzj.db;

import android.database.sqlite.SQLiteDatabase;


import com.tzj.db.annotations.FieldTag;
import com.tzj.db.annotations.FieldType;
import com.tzj.db.info.DefaultDbinfo;
import com.tzj.db.info.IDbinfo;
import com.tzj.db.info.ITabInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tzj on 2018/5/21.
 */
public class DBHelper extends BaseReflex implements ITabInfo {
    @FieldTag(type = FieldType.ignore)
    protected static final HashMap<String, SQLiteDelegate> map = new HashMap<>();
    @FieldTag(type = FieldType.ignore)
    protected IDbinfo dbinfo;

    protected Date _id = new Date();//必定有一个_id 为插入时间
    public DBHelper() {
        dbinfo = upgrade();
        if (getDbHelper() == null) {
            map.put(dbinfo.getKey(), new SQLiteDelegate(this, dbinfo));
        }
    }

    public DBHelper(Object obj) {
        //让子类 先new 后 new SQLiteDelegate
        //别忘了 dbinfo = upgrade();
    }

    public SQLiteDelegate getDbHelper() {
        return map.get(dbinfo.getKey());
    }

    //=======================================================
    @Override
    public void initFields() {
        if (sqlFiles.get(getClass()) == null) {
            initField(getClass());
        }
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
    public void close() {
        if (getDbHelper() != null) {
            getDbHelper().close();
            map.remove(dbinfo.getKey());
        }
    }

    @Override
    public IDbinfo upgrade() {
        return new DefaultDbinfo();
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