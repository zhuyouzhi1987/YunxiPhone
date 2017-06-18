package com.yunxi.phone.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/10.
 */
public class RunningBaseBean implements Serializable {
    public String getCreateTime() {
        return createTime;
    }

    public int getRun_Steps() {
        return run_Steps;
    }

    public int getRun_Integral() {
        return run_Integral;
    }

    public int getRun_ID() {
        return run_ID;
    }

    public int getStatus() {
        return status;
    }

    public String getPromptD() {
        return promptD;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setRun_Steps(int run_Steps) {
        this.run_Steps = run_Steps;
    }

    public void setRun_Integral(int run_Integral) {
        this.run_Integral = run_Integral;
    }

    public void setRun_ID(int run_ID) {
        this.run_ID = run_ID;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPromptD(String promptD) {
        this.promptD = promptD;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    private String createTime;
    private int run_Steps;
    private int run_Integral;
    private int run_ID;
    private int status;
    private String promptD;
    private String prompt;

    public int IsReceive;
}
