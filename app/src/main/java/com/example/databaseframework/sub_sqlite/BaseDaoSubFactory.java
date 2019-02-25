package com.example.databaseframework.sub_sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.example.databaseframework.db.BaseDao;
import com.example.databaseframework.db.BaseDaoFactory;

/**
 * Created by Zachary
 * on 2019/1/23.
 */
public class BaseDaoSubFactory extends BaseDaoFactory {


    private static final BaseDaoSubFactory ourInstance = new BaseDaoSubFactory();

    public static BaseDaoSubFactory getInstance() {
        return ourInstance;
    }

    private BaseDaoSubFactory() {
    }

    private SQLiteDatabase sqLiteSubDatabase;

    public <T extends BaseDao<M>,M> T getSubDao(Class<T> daoClass,Class<M> entityClass){
        BaseDao baseDao = null;
        if (map.get(PrivateDataBaseEnums.database.getValue())!=null){
            return (T) map.get(PrivateDataBaseEnums.database.getValue());
        }

        sqLiteSubDatabase = SQLiteDatabase.openOrCreateDatabase(PrivateDataBaseEnums.database.getValue(),null);
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteSubDatabase,entityClass);
            map.put(PrivateDataBaseEnums.database.getValue(),baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}
