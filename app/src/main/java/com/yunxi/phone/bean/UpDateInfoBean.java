package com.yunxi.phone.bean;

/**
 * Created by Administrator on 2017/2/9.
 */
public class UpDateInfoBean {

    public String getAndroid_version() {
        return android_version;
    }

    public String getAndroid_url() {
        return android_url;
    }

    public String getAndroid_info() {
        return android_info;
    }

    public int getForce() {
        return force;
    }

    public void setAndroid_version(String android_version) {
        this.android_version = android_version;
    }

    public void setAndroid_url(String android_url) {
        this.android_url = android_url;
    }

    public void setAndroid_info(String android_info) {
        this.android_info = android_info;
    }

    public void setForce(int force) {
        this.force = force;
    }

    private String android_version;
    private String android_url;
    private String android_info;

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    private String version_code;
    private int force;
}
