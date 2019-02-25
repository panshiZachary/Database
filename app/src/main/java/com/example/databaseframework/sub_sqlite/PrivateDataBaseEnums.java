package com.example.databaseframework.sub_sqlite;

import com.example.databaseframework.bean.User;

import com.example.databaseframework.db.BaseDaoFactory;

import java.io.File;

/**
 * Created by Zachary
 * on 2019/1/23.
 */
public enum  PrivateDataBaseEnums {

    database("");
    private String value;

    PrivateDataBaseEnums(String value){

    }

    public String getValue() {
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if (userDao!=null){
            User currentUser = userDao.getCurrentUser();
            if (currentUser!=null){
                File file = new File("data/data/com.example.databaseframework");
                if (!file.exists()){
                    file.mkdirs();
                }
                return file.getAbsolutePath()+"/"+currentUser.getId()+"_login.db";
            }

        }
        return null;
    }
}
