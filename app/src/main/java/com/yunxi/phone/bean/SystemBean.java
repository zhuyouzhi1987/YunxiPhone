package com.yunxi.phone.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
public class SystemBean {
    public String getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public List<SystemMsgBean> getData() {
        return data;
    }

    private String result;
    private String msg;

    public void setData(List<SystemMsgBean> data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private List<SystemMsgBean> data;
}
