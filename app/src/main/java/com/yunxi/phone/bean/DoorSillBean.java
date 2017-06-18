package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/22.
 */
public class DoorSillBean {
    public String getCreateTime() {
        return createTime;
    }

    public int getID() {
        return ID;
    }

    public int getDoorsill() {
        return Doorsill;
    }

    private String createTime;
    private int ID;

    public void setDoorsill(int doorsill) {
        Doorsill = doorsill;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    private int Doorsill;
}
