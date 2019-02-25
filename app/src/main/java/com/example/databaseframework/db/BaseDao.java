package com.example.databaseframework.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.databaseframework.annotation.DbField;
import com.example.databaseframework.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zachary
 * on 2019/1/16.
 */
public class BaseDao<T> implements IBaseDao<T> {
    //持有操作数据库的引用
    private SQLiteDatabase sqLiteDatabase;
    // 类的字节码
    private Class<T> entity;
    // 生成的表名
    private String tableName;
    // 是否进行初始化
    private boolean isInit = false;
    // 用来缓存表名和成员变量
    private Map<String,Field> cacheMap;


    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entity) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entity = entity;
        if (!isInit){
            if (entity.getAnnotation(DbTable.class)==null){
                tableName = entity.getName();
            }else {
                tableName = entity.getAnnotation(DbTable.class).value();
            }
            if (!sqLiteDatabase.isOpen()){
                return false;
            }
            String createTableSql = getCreateTableSql();
            sqLiteDatabase.execSQL(createTableSql);
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;
        }

        return isInit;
    }

    /**
     *  初始化map
     */
    private void initCacheMap() {
        // 1 .查询表的字段名
        String sql = "select * from "+tableName+" limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();

        //2 .获得成员变量的名
        Field[] fields = entity.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        // 3 .映射
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : fields) {
                String fieldName  =null;
                if (field.getAnnotation(DbField.class)==null){
                   fieldName = field.getName();
                }else {
                   fieldName = field.getAnnotation(DbField.class).value();
                }
                if (columnName.equals(fieldName)){
                    columnField = field;
                    break;
                }
            }
            if (columnField!=null){
                cacheMap.put(columnName,columnField);
            }

        }
    }

    /**
     * 拼接语句
     * @return
     */
    private String getCreateTableSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName+"(");
        Field[] fields = entity.getDeclaredFields();
        for (Field field : fields) {
            //拿到成员变量的类型
            Class<?> type = field.getType();
            //成员变量的有注解
            if (field.getAnnotation(DbField.class) != null) {
                if (type == String.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BLOB,");
                } else {
                    //不支持的类型号
                    continue;
                }
            } else {
                if (type == String.class) {
                    stringBuffer.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getName() + " BLOB,");
                } else {
                    //不支持的类型号
                    continue;
                }
            }
        }
        if (stringBuffer.charAt(stringBuffer.length()-1)==','){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    @Override
    public long insert(T entity) {
        Map<String,String> map = getValues(entity);
        ContentValues contentValues = getContentValues(map);
        long result = sqLiteDatabase.insert(tableName, null, contentValues);
        return result;
    }

    @Override
    public int update(T entity, T where) {
        Map<String, String> values = getValues(entity);
        ContentValues contentValues = getContentValues(values);

        Map<String, String> whereCause = getValues(where);
        Conditition conditition = new Conditition(whereCause);
        int result = sqLiteDatabase.update(tableName, contentValues, conditition.whereCause, conditition.whereArgs);
        return result;
    }

    @Override
    public int delete(T where) {
        Map<String, String> whereCause = getValues(where);
        Conditition conditition = new Conditition(whereCause);
        int result = sqLiteDatabase.delete(tableName, conditition.whereCause, conditition.whereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where,null,null,null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map<String, String> whereCause = getValues(where);
        Conditition conditition = new Conditition(whereCause);
        String limitString = null;
        if (startIndex!=null && limit!=null){
            limitString = startIndex+","+limit;
        }
        Cursor cursor = sqLiteDatabase.query(tableName, null,
                conditition.whereCause, conditition.whereArgs, null, null, orderBy, limitString);
        List<T> result= getResult(cursor,where);
        return result;
    }

    private List<T> getResult(Cursor cursor, T where) {
        List list = new ArrayList<>();
        while (cursor.moveToNext()){
            Object item = null;
            try {
                item = where.getClass().newInstance();
                Iterator<Map.Entry<String, Field>> iterator = cacheMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, Field> entry = iterator.next();
                    // 列名
                    String key = entry.getKey();
                    // 成员变量
                    Field value = entry.getValue();

                    int columnIndex = cursor.getColumnIndex(key);
                    if (columnIndex!=-1){
                        Class<?> type = value.getType();
                        if (type==String.class){
                            value.set(item,cursor.getString(columnIndex));
                        }else if (type == Integer.class){
                            value.set(item,cursor.getInt(columnIndex));
                        }else if (type == Double.class){
                            value.set(item,cursor.getDouble(columnIndex));
                        }else if (type == byte[].class){
                            value.set(item,cursor.getBlob(columnIndex));
                        }else if (type == Long.class){
                            value.set(item,cursor.getLong(columnIndex));
                        }else {
                            // 不支持的类型
                            continue;
                        }
                    }

                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        cursor.close();
        return list;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            String value = map.get(key);
            if (value!=null){
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }

    private Map<String,String> getValues(T entity) {
        Map<String,String> map = new HashMap<>();

        Iterator<Field> iterator = cacheMap.values().iterator();
        while (iterator.hasNext()){
            Field field = iterator.next();

            try {
                Object object = field.get(entity);
                if (object == null){
                    continue;
                }
                // 获取到值
                String value = object.toString();
                String key = null;
                if (field.getAnnotation(DbField.class)==null){
                    key = field.getName();
                }else {
                    key = field.getAnnotation(DbField.class).value();
                }
                if (!TextUtils.isEmpty(key)&& !TextUtils.isEmpty(value)){
                    map.put(key,value);
                }


            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;

    }
    private class Conditition{
        private String whereCause;
        private String[] whereArgs;

        public Conditition(Map<String,String> map) {
            List<String> list = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("1=1");

            Set<String> keySet = map.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = map.get(key);
                stringBuilder.append(" and "+key+"=?");
                if (value!=null){
                    list.add(value);
                }
            }
            this.whereCause = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);

        }
    }
}
