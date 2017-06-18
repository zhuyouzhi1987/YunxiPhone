package com.yunxi.phone.bean;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/2.
 */
public class SendAdsBean {
    private String result;
    private String msg;
    private ArrayList<SendAdsContentBean> data;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<SendAdsContentBean> getData() {
        return data;
    }

    public void setData(ArrayList<SendAdsContentBean> data) {
        this.data = data;
    }
}
