package com.yunxi.phone.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/22.
 */
public class ExchangeRecodeBean {
    private String result;
    private String msg;


    public void setData(ArrayList<RecodeDataBean> data) {
        this.data = data;
    }

    public ArrayList<RecodeDataBean> getData() {
        return data;
    }

    private ArrayList<RecodeDataBean> data;

    public String getResult() {
        return result;
    }
    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
