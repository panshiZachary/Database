package com.example.databaseframework.bean;

import com.example.databaseframework.annotation.DbField;
import com.example.databaseframework.annotation.DbTable;

/**
 * Created by Zachary
 * on 2019/1/16.
 */
@DbTable("tb_user")
public class User {
    @DbField("_id")
    private String id;

    private String name;

    private String password;

    private Integer status;

    public User() {
    }

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
