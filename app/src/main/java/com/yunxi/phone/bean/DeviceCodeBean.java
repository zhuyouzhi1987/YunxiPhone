package com.yunxi.phone.bean;


/**
 * Created by Administrator on 2016/4/2.
 */
public class DeviceCodeBean {
    private String result;
    private String msg;
    private DeviceCodeContentBean data;




    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DeviceCodeContentBean getData() {
        return data;
    }

    public void setData(DeviceCodeContentBean data) {
        this.data = data;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
