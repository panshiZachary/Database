package com.example.databaseframework.bean;

import com.example.databaseframework.annotation.DbTable;

/**
 * Created by Zachary
 * on 2019/1/23.
 */
@DbTable("tb_photo")
public class Photo {

    private String time;
    private String path;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
