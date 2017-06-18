package com.yunxi.phone.bean;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/2.
 */
public class TaskBean {
    private String result;
    private String msg;

    private ArrayList<TaskContentBean> data;




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


    public ArrayList<TaskContentBean> getData() {
        return data;
    }

    public void setData(ArrayList<TaskContentBean> data) {
        this.data = data;
    }
}
