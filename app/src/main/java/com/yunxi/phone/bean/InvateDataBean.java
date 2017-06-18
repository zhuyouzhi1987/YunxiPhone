package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/23.
 */
public class InvateDataBean {
    public InvateBean getData() {
        return data;
    }

    public void setData(InvateBean data) {
        this.data = data;
    }

    private InvateBean data;

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
