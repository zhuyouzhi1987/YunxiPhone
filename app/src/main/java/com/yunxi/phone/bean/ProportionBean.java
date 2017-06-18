package com.yunxi.phone.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/10.
 */
public class ProportionBean implements Serializable {
    public int getID() {
        return ID;
    }

    public int getCloudNumber() {
        return CloudNumber;
    }


    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCloudNumber(int cloudNumber) {
        CloudNumber = cloudNumber;
    }

    private int ID;
    private int CloudNumber;

    public void setPrize(int prize) {
        Prize = prize;
    }

    public int getPrize() {
        return Prize;
    }

    private int Prize;
}
