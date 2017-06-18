package com.yunxi.phone.http;


import android.content.Context;

import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.bean.DownloadInfo;
import com.yunxi.phone.bean.DownloadMax;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.TextUtil;

import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015/11/11.
 */
public class DownloadManager {
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    Map<String, DownloadTask> tasks = new HashMap<>();
    public static Map<String, DownloadTask> maxNum = new HashMap<>();
    private static DownloadManager sDownloadManager;

    public static DownloadManager getInstance() {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                sDownloadManager = new DownloadManager();
            }
        }
        return sDownloadManager;
    }

    public void download(Context context,final String url, String filename, String filepath, DownloadListener callBack) {
        final DownloadTask task;

        if (tasks.get("url") != null) {
            task = tasks.get("url");
        } else {
            task = new DownloadTask(context);
        }
        if (task.getDownloadStatus() != DownloadStatus.READY) {
            return;
        }
        executorService.submit(task.setDownloadUrl(url).setFileName(filename).setFilePath(filepath).setCallBack(callBack).setManagerCallback(new DownloadTask.CallBack() {
            @Override
            public void onFinish() {
                tasks.remove(createTag(url));
                try {
                    DownloadMax down = YunxiApplication.db.selector(DownloadMax.class).where("download_url", "=", createTag(url)).findFirst();
                    if (down != null) {
                        YunxiApplication.db.delete(down);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        }));
        L.d("下载任务开始：" + createTag(url));
        tasks.put(createTag(url), task);
        try {
            DownloadMax down = YunxiApplication.db.selector(DownloadMax.class).where("download_url", "=", createTag(url)).findFirst();
            if(down==null){
                down=new DownloadMax();
                down.setDownload_url(createTag(url));
                YunxiApplication.db.save(down);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    public void pause(String url) {
        L.d("下载任务暂停：" + createTag(url));
        DownloadTask task = tasks.get(createTag(url));
        if (task != null) {
            task.setStatus(DownloadStatus.PAUSE);
        } else {
            L.d("任务不存在");
        }
    }

    public void cancel(String url) {
        L.d("下载任务取消：" + createTag(url));
        DownloadTask task = tasks.get(createTag(url));
        if (task != null) {
            task.setStatus(DownloadStatus.CANCAL);
            tasks.remove(createTag(url));
            try {
                DownloadMax down = YunxiApplication.db.selector(DownloadMax.class).where("download_url", "=", createTag(url)).findFirst();
                if(down!=null){
                    YunxiApplication.db.delete(down);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public int getProgress(String url) {
        DownloadInfo download = null;
        try {
            download = YunxiApplication.db.selector(DownloadInfo.class).where("download_url", "=", url).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (download != null) {
            return download.getProgress();
        }
        return 0;
    }

    private static String createTag(String url) {
        return TextUtil.getMd5Value(url);
    }
}
