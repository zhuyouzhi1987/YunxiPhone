package com.yunxi.phone.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/22.
 */
public class ExchangeDataBean {
    private String integral;
    private ArrayList<DoorSillBean> doorsill;
    private ProportionBean proportion;
    public void setDoorsill(ArrayList<DoorSillBean> doorsill) {
        this.doorsill = doorsill;
    }

    public void setProportion(ProportionBean proportion) {
        this.proportion = proportion;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }



    public ArrayList<DoorSillBean> getDoorsill() {
        return doorsill;
    }

    public ProportionBean getProportion() {
        return proportion;
    }

    public String getIntegral() {
        return integral;
    }
}
