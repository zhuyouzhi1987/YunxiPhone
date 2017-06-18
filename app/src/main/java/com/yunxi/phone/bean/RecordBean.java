package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/6.
 */
public class RecordBean {
    private String name="";
    private String number="";
    private String date="";

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    private long datetime;
    private String addr="";
    private String type="";
    private int time=0;

    public String getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    private String id;

    public void setNote(String note) {
        this.note = note;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String note;

    public Boolean getIsExite() {
        return isExite;
    }

    public void setIsExite(Boolean isExite) {
        this.isExite = isExite;
    }

    private Boolean isExite;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    public String getName() {
        return name;
    }
    public String getNumber() {
        return number;
    }
    public String getDate() {
        return date;
    }
    public String getAddr() {
        return addr;
    }
    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setType(String type) {
        this.type = type;
    }


}
