package com.yunxi.phone.bean;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/2.
 */
public class TaskAddBean {
    private String result;
    private String msg;

    private ArrayList<TaskAddContentBean> data;




    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ArrayList<TaskAddContentBean> getData() {
        return data;
    }

    public void setData(ArrayList<TaskAddContentBean> data) {
        this.data = data;
    }
}
