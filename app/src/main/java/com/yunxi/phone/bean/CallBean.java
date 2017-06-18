package com.yunxi.phone.bean;


/**
 * Created by Administrator on 2016/4/2.
 */
public class CallBean {
    private String result;
    private String msg;
    private CallContentBean data;




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


    public CallContentBean getData() {
        return data;
    }

    public void setData(CallContentBean data) {
        this.data = data;
    }
}
