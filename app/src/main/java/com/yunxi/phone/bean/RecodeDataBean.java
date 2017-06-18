package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/22.
 */
public class RecodeDataBean {
    public String getNumber() {
        return Number;
    }

    public int getCloudNumber() {
        return CloudNumber;
    }

    public int getDoorsill() {
        return Doorsill;
    }

    public int getProportion() {
        return Proportion;
    }

    public int getStatus() {
        return Status;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public int getType() {
        return Type;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public void setCloudNumber(int cloudNumber) {
        CloudNumber = cloudNumber;
    }

    public void setDoorsill(int doorsill) {
        Doorsill = doorsill;
    }

    public void setProportion(int proportion) {
        Proportion = proportion;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public void setType(int type) {
        Type = type;
    }

    private String Number;
    private int CloudNumber;
    private int Doorsill;
    private int Proportion;
    private int Status;
    private String CreateTime;
    private int Type ;






}
