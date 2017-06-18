package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/23.
 */
public class InvateBean {


    public int getCountlToday() {
        return countlToday;
    }

    public int getIntegralSum() {
        return integralSum;
    }

    public int getCountSum() {
        return countSum;
    }

    public String getStore_Url() {
        return store_Url;
    }



    public void setCountlToday(int countlToday) {
        this.countlToday = countlToday;
    }

    public void setIntegralSum(int integralSum) {
        this.integralSum = integralSum;
    }

    public void setCountSum(int countSum) {
        this.countSum = countSum;
    }

    public void setStore_Url(String store_Url) {
        this.store_Url = store_Url;
    }





    private int countlToday;// = 今天的邀请renshu,
    private int integralSum;// 累计的yunduo,
    private int countSum;// 累计的人数,
    private String store_Url; //, 应用宝的url，随便填的现在

    public String getMy_Url() {
        return my_Url;
    }

    public String getQrImg_Url() {
        return qrImg_Url;
    }

    private String my_Url;//urlR,你复制的链接

    public void setQrImg_Url(String qrImg_Url) {
        this.qrImg_Url = qrImg_Url;
    }

    public void setMy_Url(String my_Url) {
        this.my_Url = my_Url;
    }

    private String qrImg_Url;//qrUrl，二维码的
}
