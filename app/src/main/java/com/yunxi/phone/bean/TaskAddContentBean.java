package com.yunxi.phone.bean;


/**
 * Created by Administrator on 2016/4/2.
 */
public class TaskAddContentBean {


    private int Task_ID;
    private String Task_Name;
    private String Task_Content;
    private String Task_Icon;

    private String Task_Package_Name;
    private String Task_Package_Url;
    private int Task_Test_Price;

    private int Task_Further_Price;

    private int Tomorrow;


    public int getTask_ID() {
        return Task_ID;
    }

    public void setTask_ID(int task_ID) {
        Task_ID = task_ID;
    }

    public String getTask_Name() {
        return Task_Name;
    }

    public void setTask_Name(String task_Name) {
        Task_Name = task_Name;
    }

    public String getTask_Content() {
        return Task_Content;
    }

    public void setTask_Content(String task_Content) {
        Task_Content = task_Content;
    }

    public String getTask_Icon() {
        return Task_Icon;
    }

    public void setTask_Icon(String task_Icon) {
        Task_Icon = task_Icon;
    }

    public String getTask_Package_Name() {
        return Task_Package_Name;
    }

    public void setTask_Package_Name(String task_Package_Name) {
        Task_Package_Name = task_Package_Name;
    }

    public String getTask_Package_Url() {
        return Task_Package_Url;
    }

    public void setTask_Package_Url(String task_Package_Url) {
        Task_Package_Url = task_Package_Url;
    }

    public int getTask_Test_Price() {
        return Task_Test_Price;
    }

    public void setTask_Test_Price(int task_Test_Price) {
        Task_Test_Price = task_Test_Price;
    }

    public int getTask_Further_Price() {
        return Task_Further_Price;
    }

    public void setTask_Further_Price(int task_Further_Price) {
        Task_Further_Price = task_Further_Price;
    }

    public int getTomorrow() {
        return Tomorrow;
    }

    public void setTomorrow(int tomorrow) {
        Tomorrow = tomorrow;
    }
}
