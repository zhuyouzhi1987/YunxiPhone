package com.yunxi.phone.service;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by xf on 2016/1/30.
 */

@Table(name="step_info")
public class StepData {

    // 指定自增，每个对象需要有一个主键
    @Column(name="id",isId = true)
    private int id;

    @Column(name="today")
    private String today;
    @Column(name="step")
    private String step;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
