package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/2/9.
 */
public class UpdateBean {
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

    public UpDateInfoBean getData() {
        return data;
    }
    public void setData(UpDateInfoBean data) {
        this.data = data;
    }
    private UpDateInfoBean data;
}
