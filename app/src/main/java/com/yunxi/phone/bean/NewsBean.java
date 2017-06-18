package com.yunxi.phone.bean;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/2.
 */
public class NewsBean {
    private String result;
    private String msg;
    private ArrayList<NewsContentBean> data;


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

    public ArrayList<NewsContentBean> getData() {
        return data;
    }

    public void setData(ArrayList<NewsContentBean> data) {
        this.data = data;
    }
}
