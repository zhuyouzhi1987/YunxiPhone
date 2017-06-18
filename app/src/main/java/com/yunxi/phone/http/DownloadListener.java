package com.yunxi.phone.http;

/**
 * Created by Administrator on 2015/11/11.
 */
public interface DownloadListener {
    abstract public void onStart(long total);

    abstract public void onProgress(long finished, long total, int progress);

    abstract public void onFinish();

    abstract public void onPause();

    abstract public void onCancel();

    /**
     * @param code 错误号
     *             404
     *             链接地址为空
     *             500
     *             服务器返回值错误
     *             501
     *             未知错误，请查看错误日志
     *             301
     *             内存卡不存在
     *             302
     *             内存卡空间不足
     *             303
     *             文件名为空
     */
    abstract public void onFail(int code);
}
