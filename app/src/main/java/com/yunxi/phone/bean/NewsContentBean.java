package com.yunxi.phone.bean;


/**
 * Created by Administrator on 2016/4/2.
 */
public class NewsContentBean {

    private int N_id;

    private String N_title;
    private String N_releaseTime;
    private String N_source;
    private int N_Category;
    private String N_imageurl1;
    private String N_imageurl2;
    private String N_imageurl3;
    private int N_isImage;
    private String my_url;

    private int is_collection;//1收藏 0未收藏


    public String getN_title() {
        return N_title;
    }

    public void setN_title(String n_title) {
        N_title = n_title;
    }

    public String getN_releaseTime() {
        return N_releaseTime;
    }

    public void setN_releaseTime(String n_releaseTime) {
        N_releaseTime = n_releaseTime;
    }

    public String getN_source() {
        return N_source;
    }

    public void setN_source(String n_source) {
        N_source = n_source;
    }

    public int getN_Category() {
        return N_Category;
    }

    public void setN_Category(int n_Category) {
        N_Category = n_Category;
    }

    public String getN_imageurl1() {
        return N_imageurl1;
    }

    public void setN_imageurl1(String n_imageurl1) {
        N_imageurl1 = n_imageurl1;
    }

    public String getN_imageurl2() {
        return N_imageurl2;
    }

    public void setN_imageurl2(String n_imageurl2) {
        N_imageurl2 = n_imageurl2;
    }

    public String getN_imageurl3() {
        return N_imageurl3;
    }

    public void setN_imageurl3(String n_imageurl3) {
        N_imageurl3 = n_imageurl3;
    }

    public int getN_isImage() {
        return N_isImage;
    }

    public void setN_isImage(int n_isImage) {
        N_isImage = n_isImage;
    }


    public String getMy_url() {
        return my_url;
    }

    public void setMy_url(String my_url) {
        this.my_url = my_url;
    }

    public int getN_id() {
        return N_id;
    }

    public void setN_id(int n_id) {
        N_id = n_id;
    }

    public int getIs_collection() {
        return is_collection;
    }

    public void setIs_collection(int is_collection) {
        this.is_collection = is_collection;
    }
}
