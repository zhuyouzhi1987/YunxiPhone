package com.yunxi.phone.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/2/7.
 */
public class LockBean implements Serializable {
    public String getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public List<LockListBean> getData() {
        return data;
    }

    private String result;
    private String msg;

    public void setData(List<LockListBean> data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private List<LockListBean> data;
}
