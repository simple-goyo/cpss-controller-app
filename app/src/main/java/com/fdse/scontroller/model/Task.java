package com.fdse.scontroller.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.sql.Timestamp;

/**
 * <pre>
 *     author : shenbiao
 *     e-mail : 1105125966@qq.com
 *     time   : 2018/08/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Task {
    private int id ;
    private String name ;
    private int userId;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Timestamp publishTime;
    private Timestamp completeTime;
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Timestamp publishTime) {
        this.publishTime = publishTime;
    }

    public Timestamp getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Timestamp completeTime) {
        this.completeTime = completeTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
