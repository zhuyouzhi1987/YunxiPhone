package com.yunxi.phone.http;

import android.content.Context;
import android.text.TextUtils;

import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.bean.DownloadInfo;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.SDCardUtils;

import org.xutils.ex.DbException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015/11/9.
 */
public class DownloadTask implements Runnable {
    private DownloadInfo download;
    private HttpURLConnection httpURLConnection;
    public DownloadStatus status = DownloadStatus.READY;
    private long finishSize = 0;
    private int progress = 0;
    private int progressTemp = 0;
    private long total = 0l;
    DownloadListener callBack;
    CallBack managerCallback;
    Context context;
    public DownloadTask(Context context) {
        this.download = new DownloadInfo();
        this.context=context;
        download.setFile_url(SDCardUtils.getSDCardPath() + "download/");
    }



    private String getRealUrl(String str) {
        try {
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.getResponseCode();
            String realUrl = conn.getURL().toString();
            conn.disconnect();
            return realUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private long getTotal(String str) {
        try {
            URL url = new URL(str);
            // 创建连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //处理下载读取长度为-1 问题
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.connect();
            // 获取文件大小
            int length = conn.getContentLength();
            conn.disconnect();
            return length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public DownloadTask setDownloadUrl(String url) {
        this.download.setDownload_url(url);
        return this;
    }

    public DownloadTask setFilePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            this.download.setFile_url(path);
        }
        return this;
    }

    public DownloadTask setFileName(String name) {
        this.download.setFile_name(name);
        return this;
    }

    public DownloadTask setCallBack(DownloadListener callBack) {
        this.callBack = callBack;
        return this;
    }

    public DownloadTask setStatus(DownloadStatus status) {
        this.status = status;
        return this;
    }

    public DownloadTask setManagerCallback(CallBack managerCallback) {
        this.managerCallback = managerCallback;
        return this;
    }

    @Override
    public void run() {
        if (TextUtils.isEmpty(download.getDownload_url())) {
            L.d("链接地址为空");
            if (callBack != null) {
                callBack.onFail(404);
            }
            return;
        }
        if (TextUtils.isEmpty(download.getFile_name())) {
            L.d("文件名称为空");
            if (callBack != null) {
                callBack.onFail(303);
            }
            return;
        }
        DownloadInfo temp = null;
        try {
//            DbManager db = YunxiApplication.daoConfig;
//            x.getDb(YunxiApplication.getD)
//            DbManager db = x.getDb(((YunxiApplication) context.getApplicationContext()).getDaoConfig());
//            DbManager db = x.getDb(YunxiApplication.getInstance().getDaoConfig());
            temp =YunxiApplication.db.selector(DownloadInfo.class).where("download_url", "=", download.getDownload_url()).findFirst();

        } catch (DbException e) {
            e.printStackTrace();
        }
        if (temp != null && download.getFile_url().equals(temp.getFile_url()) && download.getFile_name().equals(temp.getFile_name())) {
            download = temp;
            File file = new File(temp.getFile_url() + temp.getFile_name());
            if (file.exists() && download.getStatus() == 1) {
                return;
            }
            if (!file.exists()) {
                download.setFinished(0l);
                download.setProgress(0);
                download.setStatus(0);
                download.setTotal(0l);
            }
        } else {
            File file = new File(download.getFile_url() + download.getFile_name());
            if (file.exists()) {
                file.delete();
            }
            download.setFinished(0l);
            download.setProgress(0);
            download.setStatus(0);
            download.setTotal(0l);
            try {
                YunxiApplication.db.save(download);
                download =YunxiApplication.db.selector(DownloadInfo.class).where("download_url", "=", download.getDownload_url()).findFirst();
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        InputStream inputStream = null;
        RandomAccessFile raf = null;
        try {
            //获取真实地址
            String realUrl = getRealUrl(download.getDownload_url());
            if (realUrl.isEmpty()) {
                L.d("链接地址为空");
                if (callBack != null) {
                    callBack.onFail(404);
                }
                return;
            }
            total = getTotal(realUrl);
            URL url = new URL(realUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10 * 1000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Range", "bytes=" + download.getFinished() + "-");
            httpURLConnection.setRequestProperty("User-agent", System.getProperty("http.agent"));
            httpURLConnection.connect();
            if (!(httpURLConnection.getResponseCode() == 200 || httpURLConnection.getResponseCode() == 206)) {
                if (callBack != null) {
                    callBack.onFail(500);
                }
                return;
            }
            //判断sd卡是否存在，判断sd卡是否有足够空间
            if (!SDCardUtils.isSDCardEnable()) {
                L.d("内存卡不存在");
                if (callBack != null) {
                    callBack.onFail(301);
                }
                return;
            }
            if (SDCardUtils.getSDCardAllSize() < total) {
                L.d("内存卡空间不足");
                if (callBack != null) {
                    callBack.onFail(302);
                }
                return;
            }

            inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            File dir = new File(download.getFile_url());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(download.getFile_url() + download.getFile_name());
            if (total != download.getTotal()) {
                download.setTotal(total);
                download.setStatus(0);
                download.setFinished(0l);
                download.setProgress(0);
                download.getDownload_url();
                YunxiApplication.db.update(download);
                if (file.exists()) {
                    file.delete();
                }
            }
            raf = new RandomAccessFile(file, "rwd");
            raf.setLength(total);
            finishSize = download.getFinished();
            raf.seek(download.getFinished());
            byte[] buffer = new byte[4096];
            int length = -1;
            if (status == DownloadStatus.CANCAL) {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                }
                return;
            }
            status = DownloadStatus.DOWNLOAD;
            if (callBack != null) {
                callBack.onStart(total);
            }
            while ((length = inputStream.read(buffer)) != -1) {
                raf.write(buffer, 0, length);
                finishSize += length;
                progressTemp = (int) (finishSize * 100 / total);
                if (progress < progressTemp) {
                    progress = progressTemp;
                    // 更新数据库中的下载信息
                    download.setFinished(finishSize);
                    download.setProgress(progress);
                    YunxiApplication.db.update(download);
                    if (progress > 100) {
                        file.delete();
                        YunxiApplication.db.delete(download);
                        callBack.onFail(501);
                        break;
                    }
                    if (callBack != null) {
                        callBack.onProgress(finishSize, total, progress);
                    }
                }
                if (status == DownloadStatus.PAUSE) {
                    break;
                }
                if (status == DownloadStatus.CANCAL) {
                    file.delete();
                    YunxiApplication.db.delete(download);
                    break;
                }
            }
            if (finishSize == total) {
                download.setStatus(1);
                YunxiApplication.db.update(download);
                status = DownloadStatus.FINISH;
                if (callBack != null) {
                    callBack.onFinish();
                }
                if (managerCallback != null) {
                    managerCallback.onFinish();
                }
            } else {
                if (status == DownloadStatus.DOWNLOAD) {
                    run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onFail(501);
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (raf != null) {
                    raf.close();
                }
                httpURLConnection.disconnect();
                switch (status) {
                    case CANCAL:
                        if (callBack != null) {
                            callBack.onCancel();
                        }
                        break;
                    case PAUSE:
                        if (callBack != null) {
                            callBack.onPause();
                            status = DownloadStatus.READY;
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface CallBack {
        abstract public void onFinish();
    }

    public DownloadStatus getDownloadStatus() {
        return status;
    }

}
