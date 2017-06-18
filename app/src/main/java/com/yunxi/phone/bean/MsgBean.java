package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/13.
 */
public class MsgBean {
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
