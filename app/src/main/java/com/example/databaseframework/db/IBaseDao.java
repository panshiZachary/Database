package com.example.databaseframework.db;

import java.util.List;

/**
 * Created by Zachary
 * on 2019/1/16.
 */
public interface IBaseDao<T>  {
    /**
     * 插入数据
     * @param entity
     * @return
     */
    long insert(T entity);

    /**
     * 修改数据
     * @param entity
     * @param where
     * @return
     */
    int update(T entity,T where);

    /**
     * 删除
     * @param where
     * @return
     */
    int delete(T where);

    /**
     * 查询
     * @param where
     * @return
     */
    List<T> query(T where);

    /**
     *
     * @param where
     * @param orderBy  排序
     * @param startIndex
     * @param limit
     * @return
     */
    List<T> query(T where,String orderBy,Integer startIndex,Integer limit);
}
