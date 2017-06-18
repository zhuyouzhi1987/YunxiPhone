package com.yunxi.phone.bean;


/**
 * Created by Administrator on 2016/4/2.
 */
public class CallContentBean {

    public String getTalkminutes() {
        return talkminutes;
    }

    public String getProportion() {
        return proportion;
    }

    public String getColudnumber() {
        return coludnumber;
    }

    public void setColudnumber(String coludnumber) {
        this.coludnumber = coludnumber;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public void setTalkminutes(String talkminutes) {
        this.talkminutes = talkminutes;
    }

    private String talkminutes;
    private String proportion;
    private String coludnumber;

}
