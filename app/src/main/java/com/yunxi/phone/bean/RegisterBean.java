package com.yunxi.phone.bean;


/**
 * Created by Administrator on 2016/4/2.
 */
public class RegisterBean {
    private String result;
    private String msg;
    private RegisterContentBean data;




    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public RegisterContentBean getData() {
        return data;
    }

    public void setData(RegisterContentBean data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
