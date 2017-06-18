package com.yunxi.phone.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2017/2/7.
 */
@Table(name="lockinfo")
public class LockInfo {

    @Column(name="id",isId = true)
    private Long id;
    @Column(name="phoneUrl")
    private String phoneUrl;
    @Column(name="packetUrl")
    private String packetUrl;
    @Column(name="lockUrl")
    private String lockUrl;
    @Column(name="lockId")
    private int lockId;
    @Column(name="bgUrl")
    private String bgUrl;
    @Column(name="isUse")
    private String isUse;//1使用中


    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }
    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }
    public Long getId() {
        return id;
    }

    public String getPhoneUrl() {
        return phoneUrl;
    }

    public String getPacketUrl() {
        return packetUrl;
    }

    public String getLockUrl() {
        return lockUrl;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPhoneUrl(String phoneUrl) {
        this.phoneUrl = phoneUrl;
    }

    public void setPacketUrl(String packetUrl) {
        this.packetUrl = packetUrl;
    }

    public void setLockUrl(String lockUrl) {
        this.lockUrl = lockUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }


}
