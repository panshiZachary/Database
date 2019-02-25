package com.example.databaseframework.sub_sqlite;

import com.example.databaseframework.bean.User;
import com.example.databaseframework.db.BaseDao;

import java.util.List;

/**
 * Created by Zachary
 * on 2019/1/23.
 */
public class UserDao extends BaseDao<User> {
    /**
     * 用户登录时 插入数据
     * @param entity
     * @return
     */
    @Override
    public long insert(User entity) {
        // 遍历所有数据 status 设为0
        List<User> list = query(new User());
        for (User user : list) {
            User where = new User();
            where.setId(user.getId());
            user.setStatus(0);
            update(user,where);
        }
        // 将新登录的用户状态改为1
        entity.setStatus(1);
        return super.insert(entity);
    }

    /**
     *  获得当前登录用户
     * @return
     */
    public User getCurrentUser(){
        User where = new User();
        where.setStatus(1);
        List<User> list = query(where);
        if (list.size()>0){
            return list.get(0);
        }
        return null;

    }
}
