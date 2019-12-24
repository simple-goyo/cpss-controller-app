package com.fdse.scontroller.model;

/**
 * <pre>
 *     author : shenbiao
 *     e-mail : 1105125966@qq.com
 *     time   : 2018/07/26
 *     desc   :自定义item,设备实体类
 *     version: 1.0
 * </pre>
 */
public class HomeDevice {
    private int imageId;
    private String name;
    private  String state;

    public HomeDevice(int imageId,String name,String state ) {
        this.imageId = imageId;
        this.name = name;
        this.state = state;
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
