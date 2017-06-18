package com.yunxi.phone.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/10.
 */
public class IntegralBean implements Serializable {
    public String getSteps() {
        return Steps;
    }

    public String getIntegral() {
        return Integral;
    }



    public void setSteps(String steps) {
        Steps = steps;
    }

    public void setIntegral(String integral) {
        Integral = integral;
    }


    private String Steps;

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    private String CreateTime;
    private String Integral;
}
