package com.yunxi.phone.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/10.
 */
public class RunDataBean implements Serializable{
    private static final long serialVersionUID = 1L;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    private String datetime;
    public ArrayList<IntegralBean> getIntegral() {
        return integral;
    }

    public ArrayList<RunningBaseBean> getRunningbase() {
        return runningbase;
    }

    private ArrayList<IntegralBean> integral;

    public void setRunningbase(ArrayList<RunningBaseBean> runningbase) {
        this.runningbase = runningbase;
    }

    public void setIntegral(ArrayList<IntegralBean> integral) {
        this.integral = integral;
    }

    private ArrayList<RunningBaseBean> runningbase;

    public ProportionBean getProportion() {
        return proportion;
    }

    public void setProportion(ProportionBean proportion) {
        this.proportion = proportion;
    }

    private ProportionBean proportion;

}
