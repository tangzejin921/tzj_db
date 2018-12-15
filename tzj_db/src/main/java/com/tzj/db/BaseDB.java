package com.tzj.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseDB extends DBHelper {

    public BaseDB() {
        super();
    }

    public BaseDB(Object obj) {
        super(obj);
    }

    public long insert(String where, Object... data) {
        return insert(null, where, data);
    }

    /**
     * 增
     * 有所给条件的内容的话改为update
     *
     * @param where 为NULL时表明不管有没有直接插入
     *              为""时表明查看有无完全一样的没有再插入
     */
    public long insert(ContentValues values, String where, Object... strs) {
        if (values == null) {
            values = contentValues();
        }
        if (values.size() <= 0) {
            return 0;
        }
        //确保用当前时间插入,如果插入在一微妙内完成多条数据，那么会出现多个相同的_id
        values.put("_id", System.currentTimeMillis());
        if (!TextUtils.isEmpty(where)) {
            String[] temp = null;
            if (strs != null) {
                temp = new String[strs.length];
                for (int i = 0; i < strs.length; i++) {
                    temp[i] = strs[i].toString();
                }
            }
            if (count(where, temp) > 0) {
                return update(where, temp);
            }
        }
        return getDbHelper().getWritableDatabase().insert(tabName(), null, values);
    }

    /**
     * 删
     *
     * @param where 为NULL时删除_id
     * @param data
     * @return: void
     */
    public int delete(String where, String... data) {
        if (TextUtils.isEmpty(where)) {
            where = "_id=?";
            data = new String[]{_id + ""};
        }
        return getDbHelper().getWritableDatabase().delete(tabName(), where, data);
    }

    /**
     * 转化为 where
     */
    public Where where(String where, Object... data) {
        return new Where(this, where, data);
    }

    /**
     * 数量
     */
    public int count(String where, String... data) {
        String sql = null;
        if (TextUtils.isEmpty(where)) {
            sql = "select count(_id) from " + tabName();
        } else if (where.contains("=") || where.contains(">") || where.contains("<")){
            sql = "select count(_id) from " + tabName() + " where " + where;
        }else{
            sql = "select count(_id) from " + tabName() + " " + where;
        }
        Cursor cursor = getDbHelper().getReadableDatabase().rawQuery(sql, data);
        cursor.moveToNext();
        int columnIndex = cursor.getColumnIndex("count(_id)");
        int anInt = cursor.getInt(columnIndex);
        cursor.close();
        return anInt;
    }

    /**
     * 查一条,并将自身填充
     */
    public <T extends BaseDB> T selectFirst(String where, String... data) {
        String sql = null;
        if (TextUtils.isEmpty(where)) {
            sql = "select * from " + tabName() + " limit 1";
        } else if (where.contains("=") || where.contains(">") || where.contains("<")){
            sql = "select * from " + tabName() + " where " + where + " limit 1";
        } else {
            sql = "select * from " + tabName() + " " + where + " limit 1";
        }
        List<BaseDB> ret = new ArrayList<>();
        Cursor cursor = getDbHelper().getReadableDatabase().rawQuery(sql, data);
        if (cursor == null) {
            cursor.close();
            return null;
        }
        while (cursor.moveToNext()) {
            try {
                filling(this, getClass(), cursor);
                ret.add(this);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        if (ret.size() > 0) {
            return (T) ret.get(0);
        } else {
            return null;
        }
    }

    /**
     * 查多条
     *
     * @param where 为NULL时查询所有
     */
    public <T extends BaseDB> List<T> select(String where, String... data) {
        String sql = null;
        if (TextUtils.isEmpty(where)) {
            sql = "select * from " + tabName();
        } else if (where.contains("=") || where.contains(">") || where.contains("<")){
            sql = "select * from " + tabName() + " where " + where;
        } else {
            sql = "select * from " + tabName() + " " + where;
        }
        Cursor cursor = getDbHelper().getReadableDatabase().rawQuery(sql, data);
        if (cursor == null) {
            close();
            return new ArrayList<>();
        }
        return toList(cursor);
    }

    /**
     *
     */
    public int update(String where, String... data) {
        return update(null, where, data);
    }

    /**
     * 改
     *
     * @param where 不能为空
     */
    public int update(ContentValues values, String where, String... data) {
        if (values == null) {
            values = contentValues();
        }
        if (values.size() <= 0) {
            return 0;
        }
        return getDbHelper().getWritableDatabase().update(tabName(), values, where, data);
    }

    /**
     * 类转化为 ContentValues
     */
    protected ContentValues contentValues(){
        getDbHelper().getWritableDatabase();//
        Map<String, Object> map = toValue(getClass());
        Parcel obtain = Parcel.obtain();
        obtain.writeMap(map);
        obtain.setDataPosition(0);
        ContentValues values = ContentValues.CREATOR.createFromParcel(obtain);
        obtain.recycle();
        return values;
    }

    protected <T extends BaseDB> List<T> toList(Cursor cursor){
        List ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            try {
                BaseDB baseDB = getClass().newInstance();
                filling(baseDB, getClass(), cursor);
                ret.add(baseDB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return ret;
    }
}