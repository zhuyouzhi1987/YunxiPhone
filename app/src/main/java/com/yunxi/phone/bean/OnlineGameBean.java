package com.yunxi.phone.bean;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/2.
 */
public class OnlineGameBean {
    private String result;
    private int count;
    private ArrayList<OnlineGameContentBean> data;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<OnlineGameContentBean> getData() {
        return data;
    }

    public void setData(ArrayList<OnlineGameContentBean> data) {
        this.data = data;
    }
}
