package com.example.databaseframework.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zachary
 * on 2019/1/16.
 */
public class BaseDaoFactory {

    private SQLiteDatabase sqLiteDatabase;
    private String sqLiteDatabasePath;
    public Map<String,BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    private static final BaseDaoFactory ourInstance = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return ourInstance;
    }

    protected BaseDaoFactory() {
        sqLiteDatabasePath = "data/data/com.example.databaseframework/zachary.db";

        sqLiteDatabase =SQLiteDatabase.openOrCreateDatabase(sqLiteDatabasePath,null);



    }

    public synchronized  <T extends BaseDao<M>,M> T getBaseDao(Class<T> daoClass,Class<M> entity){
        BaseDao baseDao =null;
        if (map.get(daoClass.getSimpleName())!=null){
            return (T) map.get(daoClass.getSimpleName());
        }

        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase,entity);
            map.put(daoClass.getSimpleName(),baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;

    }









}
