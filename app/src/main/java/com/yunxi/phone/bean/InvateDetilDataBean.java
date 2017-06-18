package com.yunxi.phone.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/23.
 */
public class InvateDetilDataBean {


    public ArrayList<InvateDetilBean> getData() {
        return data;
    }

    public void setData(ArrayList<InvateDetilBean> data) {
        this.data = data;
    }

    private ArrayList<InvateDetilBean> data;
    public String getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    private String result;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private String msg;
}
