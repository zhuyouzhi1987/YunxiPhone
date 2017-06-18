package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/23.
 */
public class InvateDetilBean {
    public String getCreateTime() {
        return CreateTime;
    }

    public String getPhone() {
        return Phone;
    }

    public String getIntegral() {
        return Integral;
    }

    private String CreateTime;

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setIntegral(String integral) {
        Integral = integral;
    }

    private String Phone;
    private String Integral;
}
