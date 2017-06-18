package com.yunxi.phone.bean;


/**
 * Created by Administrator on 2016/4/2.
 */
public class CloudBean {
    private String result;
    private String msg;
    private CloudContentBean data;




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


    public CloudContentBean getData() {
        return data;
    }

    public void setData(CloudContentBean data) {
        this.data = data;
    }
}
