package com.yunxi.phone.bean;

import com.yunxi.phone.bean.ExchangeDataBean;

/**
 * Created by Administrator on 2017/1/22.
 */
public class ExchangeBean {

    private String result;
    private String msg;

    public ExchangeDataBean getData() {
        return data;
    }

    public void setData(ExchangeDataBean data) {
        this.data = data;
    }

    private ExchangeDataBean data;

    public String getResult() {
        return result;
    }
    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
