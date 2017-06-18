package com.yunxi.phone.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/7.
 */
public class LockListBean implements Serializable{
    public String getBackground_Img() {
        return Background_Img;
    }

    public String getPhone_Img() {
        return Phone_Img;
    }

    public String getRedEnvelop_Img() {
        return RedEnvelop_Img;
    }

    public String getUnlock_Img() {
        return Unlock_Img;
    }

    public String getThumbnail_Img() {
        return Thumbnail_Img;
    }

    public int getID() {
        return ID;
    }

    public void setBackground_Img(String background_Img) {
        Background_Img = background_Img;
    }

    public void setPhone_Img(String phone_Img) {
        Phone_Img = phone_Img;
    }

    public void setRedEnvelop_Img(String redEnvelop_Img) {
        RedEnvelop_Img = redEnvelop_Img;
    }

    public void setUnlock_Img(String unlock_Img) {
        Unlock_Img = unlock_Img;
    }

    public void setThumbnail_Img(String thumbnail_Img) {
        Thumbnail_Img = thumbnail_Img;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private String Background_Img;
    private String Phone_Img;
    private String RedEnvelop_Img;
    private String Unlock_Img;
    private String Thumbnail_Img;
    private int ID;
}
