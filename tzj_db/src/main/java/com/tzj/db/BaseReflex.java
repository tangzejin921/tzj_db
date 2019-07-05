package com.tzj.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.tzj.db.annotations.FieldTag;
import com.tzj.db.annotations.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tzj on 2018/5/21.
 */
public class BaseReflex {
    /**
     * key-原生情况下是 Class
     * key-Flutter  情况下是 dbPath()+dbName()+tabName()
     */
    @FieldTag(type = FieldType.ignore)
    public static Map<Object,List<SqlField>> sqlFiles = new HashMap<>();

    /**
     * 初始化这个类
     */
    protected List<SqlField> initField(Class<?> c){
        List<SqlField> sqlFieldList = sqlFiles.get(getClass());
        if (sqlFieldList == null){
            sqlFieldList = new ArrayList<>();
            sqlFiles.put(c,sqlFieldList);
        }
        //递归结束标志
        if (c == BaseReflex.class) {
            return sqlFieldList;
        }
        Field[] fields = c.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                //去除 static
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);


                String name = field.getName();
                Class<?> type = field.getType();//class 类型
                String sqlType = field.getType().getSimpleName();
                Object initValue = null;
                try {
                    initValue = field.get(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                FieldTag annotation = field.getAnnotation(FieldTag.class);
                if (annotation!=null){
                    FieldType annotationType = annotation.type();
                    sqlType = annotationType.name().toLowerCase();
                    if (annotationType == FieldType.ignore) {//被忽略的跳过
                        continue;
                    } else if (annotationType == FieldType.VARCHAR) {
                        sqlType = "varchar(" + annotation.length() + ")";
                    }
                    if (annotation.isKey()) {//主键
                        sqlType += " primary key";
                    }
                }else {
                    //这里强行改掉
                    if (type == String.class){//String 用text存
                        sqlType = "text";
                    }else if (type == Date.class){//Date
                        sqlType = "datetime";
                    }
                }
                if (initValue != null){
                    if (initValue instanceof Date){
                        initValue = "(now())";
                    }else if (initValue instanceof String){
                        if (TextUtils.isEmpty((String)initValue)){
                            initValue = "''";
                        }else{
                            initValue = "'"+initValue+"'";//这里防止字符串里有特殊字符 如 ,
                        }
                    }
                    sqlType += " default "+initValue;
                }
                SqlField sqlField = new SqlField();
                sqlField.setName(name);
                sqlField.setInitValue(initValue);
                sqlField.setSqlType(sqlType);
                sqlField.setType(type);

                sqlFieldList.add(sqlField);
            }
        }
        c = c.getSuperclass();
        return initField(c);
    }
    /**
     * 将类转成map<name,value>
     */
    protected ContentValues toValue(Class<?> c) {
        ContentValues map = new ContentValues();
        List<SqlField> sqlFields = sqlFiles.get(c);
        //可能出现 NUll，TODO 猜测是 fastJson 导致的，没具体验证
        if (sqlFields == null){
            sqlFields = initField(c);
        }
        for (SqlField sf:sqlFields) {
            try {
                Field field = getField(c,sf.getName());
                if (field == null){
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(this);
                if (value instanceof Date){
                    value = ((Date) value).getTime();
                }
                addToContentValue(map,sf.getName(),value);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 类填充value
     * cursor 不要 关闭
     */
    protected void filling(Object obj, Class c, Cursor cursor) {
        List<SqlField> sqlFields = sqlFiles.get(c);
        if (sqlFields == null){
            sqlFields = initField(c);
        }
        for (SqlField sf:sqlFields) {
            try {
                Field field = getField(c,sf.getName());
                field.setAccessible(true);
                Object value = sf.getCursorValue(cursor);
                field.set(obj, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Field getField(Class c,String name){
        Field field = null;
        Class temp = c;
        while (field == null && temp != BaseReflex.class){
            try {
                field = temp.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                temp = temp.getSuperclass();
            }
        }
        return field;
    }

    public static class SqlField {
        /**
         * 字段名
         */
        private String name;
        /**
         * 字段类型
         */
        private Class<?> type;
        /**
         * sql的字段类型
         */
        private String sqlType;
        /**
         * 初始值
         */
        private Object initValue;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }

        public String getSqlType() {
            return sqlType;
        }

        public void setSqlType(String sqlType) {
            this.sqlType = sqlType;
        }

        public Object getInitValue() {
            return initValue;
        }

        public void setInitValue(Object initValue) {
            this.initValue = initValue;
        }

        public Object getCursorValue(Cursor cursor){
            SqlField sf = this;
            Class<?> type = sf.getType();
            int columnIndex = cursor.getColumnIndex(sf.getName());
            Object value = null;
            if (type == String.class) {
                value = cursor.getString(columnIndex);
            } else if (type == Integer.class || type == Integer.TYPE) {
                value = cursor.getInt(columnIndex);
            } else if (type == Boolean.class || type == Boolean.TYPE) {
                int i = cursor.getInt(columnIndex);
                if (i == 0){
                    value = false;
                }else{
                    value = true;
                }
            } else if (type == Byte.class || type == Byte.TYPE) {
                value = (byte)cursor.getInt(columnIndex);
            } else if (type == Float.class || type == Float.TYPE) {
                value = cursor.getFloat(columnIndex);
            } else if (type == Long.class || type == Long.TYPE) {
                value = cursor.getLong(columnIndex);
            } else if (type == Double.class || type == Double.TYPE) {
                value = cursor.getDouble(columnIndex);
            } else if (type == Date.class) {//这里要转换
                long time = cursor.getLong(columnIndex);
                value = new Date(time);
            }
            return value;
        }
    }

    /**
     * android 29 通过 Parcel 加入失败。
     * 加入ContentValues
     */
    public static boolean addToContentValue(ContentValues values,String key,Object obj){
        if (obj == null){
            return false;
        }
        if (obj instanceof String){
            values.put(key,(String) obj);
        }else if (obj instanceof Integer){
            values.put(key,(Integer) obj);
        }else if (obj instanceof Boolean){
            values.put(key,(Boolean) obj);
        }else if (obj instanceof Byte){
            values.put(key,(Byte) obj);
        }else if (obj instanceof Float){
            values.put(key,(Float) obj);
        }else if (obj instanceof Long){
            values.put(key,(Long) obj);
        }else if (obj instanceof Double){
            values.put(key,(Double) obj);
        }else if (obj instanceof Date){
            values.put(key,((Date)obj).getTime());
        }else{
            return false;
        }
        return true;
    }

    public static void main(String[] args){
        String s = new Date().toString();
        System.out.println(s);
    }
}
