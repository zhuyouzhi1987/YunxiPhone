package com.yunxi.phone.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2015/8/7.
 */
@Table(name="download")
public class Download {
    @Column(name="id",isId = true)
    private int id;
    @Column(name="task_id")
    private int task_id;
    @Column(name="ad_id")
    private int ad_id;
    @Column(name="url")
    private String url;
    @Column(name="filename")
    private String filename;
    @Column(name="progress")
    private int progress;
    @Column(name="finish")
    private int finish;
    @Override
    public String toString() {
        return "Download{" +
                "task_id=" + task_id +
                ", ad_id=" + ad_id +
                ", url='" + url + '\'' +
                ", filename='" + filename + '\'' +
                ", progress=" + progress +
                ", finish=" + finish +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 【0 下载中 1 成功 2 失败】
     */


    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public int getAd_id() {
        return ad_id;
    }

    public void setAd_id(int ad_id) {
        this.ad_id = ad_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getFinish() {
        return finish;
    }

    /**
     * 【0 下载中 1 成功 2 失败】
     */
    public void setFinish(int finish) {
        this.finish = finish;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
