package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/1/13.
 */
public class SystemMsgBean {
    private int Notice_ID;

    public int getNotice_ID() {
        return Notice_ID;
    }

    public String getNotice_Title() {
        return Notice_Title;
    }

    public String getNotice_Info() {
        return Notice_Info;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public int getNotice_IsShow() {
        return Notice_IsShow;
    }

    public void setNotice_Title(String notice_Title) {
        Notice_Title = notice_Title;
    }

    public void setNotice_Info(String notice_Info) {
        Notice_Info = notice_Info;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public void setNotice_IsShow(int notice_IsShow) {
        Notice_IsShow = notice_IsShow;
    }

    public void setNotice_ID(int notice_ID) {
        Notice_ID = notice_ID;
    }

    private String Notice_Title;
    private String Notice_Info;
    private String CreateDate;
    private int Notice_IsShow;
}
