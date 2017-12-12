package com.example.phoneshow.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/12/9 0009.
 */

public class PhoneInfoBean {
    private String id;

    private String name;

    private String phoneNum;

    private Bitmap phoneIcon;

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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Bitmap getPhoneIcon() {
        return phoneIcon;
    }

    public void setPhoneIcon(Bitmap phoneIcon) {
        this.phoneIcon = phoneIcon;
    }

    @Override
    public String toString() {
        return "PhoneInfoBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", phoneIcon=" + phoneIcon +
                '}';
    }
}
