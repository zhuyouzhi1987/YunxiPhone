package com.yunxi.phone.bean;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/2.
 */
public class CloudContentBean {


    private CloudContentForJifenBean user;

    private ArrayList<CloudContentForBannerBean> imgresult;

    private String notice;


    public CloudContentForJifenBean getUser() {
        return user;
    }

    public void setUser(CloudContentForJifenBean user) {
        this.user = user;
    }

    public ArrayList<CloudContentForBannerBean> getImgresult() {
        return imgresult;
    }

    public void setImgresult(ArrayList<CloudContentForBannerBean> imgresult) {
        this.imgresult = imgresult;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}
