package com.yunxi.phone.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.bean.Download;
import com.yunxi.phone.bean.TaskBean;
import com.yunxi.phone.http.DownloadListener;
import com.yunxi.phone.http.DownloadManager;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.XUtil;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/2/12.
 */
public class DownloadService extends Service {
    public static DownloadService service = null;
    private static final String TAG = "DownloadService";
    private IBinder binder = new LocalBinder();
    private String currentTempFilePath = Environment
            .getExternalStorageDirectory() + "/pf/apps/";
    public String appfilename = "temp.apk";
    public String download_url = "";
    public int ad_id;
    public int task_id;
    String pachage_name;
    public boolean flag = true;
    private final String module_add = "download/add";
    private final String module = "task/testing";
    public Map<Integer, Integer> taskType = new HashMap<>();
    int isWho;//0 体验 1追加
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
    }

    public void download(final String appfilename, final String download_url, final int ad_id, final int task_id) {
        DownloadManager.getInstance().download(getApplicationContext(),download_url, appfilename, currentTempFilePath, new DownloadListener() {
            @Override
            public void onStart(long total) {
                addDownload(task_id);
                try {
//                    Download download = TTHApplication.db.findFirst(Selector.from(Download.class).where("task_id", "=", task_id));
//                    DbManager db = x.getDb(((YunxiApplication) getApplicationContext()).getDaoConfig());
                    Download download = YunxiApplication.db.selector(Download.class).where("task_id", "=", task_id).findFirst();

                    if (download == null) {
                        download = new Download();
                        download.setAd_id(ad_id);
                        download.setTask_id(task_id);
                        download.setUrl(download_url);
                        download.setFilename(appfilename);
                        download.setFinish(0);
                        download.setProgress(0);
//                        TTHApplication.db.save(download);
                        YunxiApplication.db.save(download);
                    } else {
                        download.setAd_id(ad_id);
                        download.setTask_id(task_id);
                        download.setUrl(download_url);
                        download.setFilename(appfilename);
                        download.setFinish(0);
//                        TTHApplication.db.update(download);
                        YunxiApplication.db.update(download);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                //设置Intent的Action属性
                intent.putExtra("task_id", task_id);
                intent.putExtra("type", 1);
                taskType.put(task_id, 1);
                intent.setAction("com.pingfu.download");
                //发送广播
                sendBroadcast(intent);
            }

            @Override
            public void onProgress(long finished, long total, int progress) {
                try {
//                    Download download = TTHApplication.db.findFirst(Selector.from(Download.class).where("task_id", "=", task_id));
//                    DbManager db = x.getDb(((YunxiApplication) getApplicationContext()).getDaoConfig());
                    L.d(progress+"//"+total);
                    Download download = YunxiApplication.db.selector(Download.class).where("task_id", "=", task_id).findFirst();
                    download.setAd_id(ad_id);
                    download.setTask_id(task_id);
                    download.setUrl(download_url);
                    download.setFinish(0);
                    download.setProgress(progress);
                    YunxiApplication.db.update(download);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                //设置Intent的Action属性
                intent.putExtra("task_id", task_id);
                intent.putExtra("type", 2);
                taskType.put(task_id, 2);
                intent.putExtra("progress", progress);
//                if(isWho==0){
//                    intent.setAction("com.pingfu.downloadti");
//                }else if(isWho==1){
//                    intent.setAction("com.pingfu.downloadzhui");
//                }
                intent.setAction("com.pingfu.download");
                //发送广播
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                try {
//                    Download download = TTHApplication.db.findFirst(Selector.from(Download.class).where("task_id", "=", task_id));
//                    DbManager db = x.getDb(((YunxiApplication) getApplicationContext()).getDaoConfig());
                    Download download = YunxiApplication.db.selector(Download.class).where("task_id", "=", task_id).findFirst();
                    download.setAd_id(ad_id);
                    download.setTask_id(task_id);
                    download.setUrl(download_url);
                    download.setFinish(1);
                    download.setProgress(100);
                    YunxiApplication.db.update(download);
                } catch (DbException e) {
                    e.printStackTrace();
                }
//                goTask(ad_id, task_id);
                File myTempFile = new File(currentTempFilePath
                        + appfilename);
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                // 获得下载好的文件类型
                String type = getMIMEType(myTempFile);
                // 打开各种类型文件
                intent.setDataAndType(Uri.fromFile(myTempFile), type);
                Intent dintent = new Intent();
                //设置Intent的Action属性
                dintent.setAction("com.pingfu.download");
                dintent.putExtra("task_id", task_id);
                dintent.putExtra("type", 3);
                taskType.put(task_id, 3);
                dintent.putExtra("progress", 100);
                //发送广播
                sendBroadcast(dintent);
                openFile(myTempFile);
            }

            @Override
            public void onPause() {
                Intent intent = new Intent();
                //设置Intent的Action属性
                intent.putExtra("task_id", task_id);
                intent.putExtra("type", 4);
                taskType.put(task_id, 4);
                intent.setAction("com.pingfu.download");
                //发送广播
                sendBroadcast(intent);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onFail(int code) {
                try {
//                    Download download = TTHApplication.db.findFirst(Selector.from(Download.class).where("task_id", "=", task_id));
//                    DbManager db = x.getDb(((YunxiApplication) getApplicationContext()).getDaoConfig());
                    Download download = YunxiApplication.db.selector(Download.class).where("task_id", "=", task_id).findFirst();
                    if (download == null) {
                        download = new Download();
                        download.setAd_id(ad_id);
                        download.setTask_id(task_id);
                        download.setUrl(download_url);
                        download.setFinish(2);
                        download.setProgress(0);
                        YunxiApplication.db.save(download);
                    } else {
                        download.setAd_id(ad_id);
                        download.setTask_id(task_id);
                        download.setUrl(download_url);
                        download.setFinish(2);
                        download.setProgress(0);
                        YunxiApplication.db.update(download);
                    }
                    Intent intent = new Intent();
                    //设置Intent的Action属性
                    intent.putExtra("task_id", task_id);
                    intent.putExtra("type", -1);
                    taskType.put(task_id, -1);
                    intent.setAction("com.pingfu.download");
                    //下载失败 重试的时候 处理操作
                    //发送广播
                    sendBroadcast(intent);
                } catch (DbException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            appfilename = intent.getStringExtra("appfilename");
            download_url = intent.getStringExtra("download_url");
            pachage_name = intent.getStringExtra("pachage_name");
            ad_id = intent.getIntExtra("ad_id", 0);
            task_id = intent.getIntExtra("task_id", 0);

            isWho = intent.getIntExtra("isWho", -1);
            download(appfilename, download_url, ad_id, task_id);
        } catch (Exception e) {
        }
        return START_STICKY;
    }


    private void addDownload(int task_id) {
//        RequestParams params = new RequestParams();
//        StringBuilder sb = new StringBuilder();
//        sb.append(TTHApplication.phone);
//        sb.append("|");
//        sb.append(TTHApplication.userkey);
//        sb.append("|");
//        sb.append(task_id);
//        params.addQueryStringParameter("info", TextUtil.yihuojiami(sb.toString(), UrlUtil.url_key));
//        L.d(UrlUtil.url_base + module_add + "?info=" + TextUtil.yihuojiami(sb.toString(), UrlUtil.url_key));
//        L.d(UrlUtil.url_base + module_add + "?info=" + sb.toString());
//        TTHApplication.http.configUserAgent(TextUtil.getUserAgent());
//        TTHApplication.http.send(HttpRequest.HttpMethod.GET, UrlUtil.url_base + module_add, params, new RequestCallBack<String>() {
//
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                L.d("添加下载记录：" + responseInfo.result);
//            }
//
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//            }
//        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(getApplicationContext(), DownloadService.class));
        service = null;
        super.onDestroy();
    }


    //定义内容类继承Binder
    public class LocalBinder extends Binder {
        //返回本地服务
        DownloadService getService() {
            return DownloadService.this;
        }
    }

    /**
     * 打开文件进行安装
     *
     * @param f
     */
    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        // 获得下载好的文件类型
        String type = getMIMEType(f);
        // 打开各种类型文件
        intent.setDataAndType(Uri.fromFile(f), type);
        // 安装
        startActivity(intent);
    }


    /**
     * 获得下载文件的类型
     *
     * @param f 文件名称
     * @return 文件类型
     */
    private String getMIMEType(File f) {
        String type = "";
        // 获得文件名称
        String fName = f.getName();
        // 获得文件扩展名
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }

    /**
     * 完成下载任务
     */


    //加载体验数据
    private void goTask(int page,int task_id) {

        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(this);

        L.d("uid====" + mCache.getAsString("user_id"));
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        map.put("task_id",task_id);
        map.put("package_name",pachage_name);
        XUtil.Post(this, AddressApi.SEDN_TASK, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                L.d(result);
                TaskBean bean = JSON.parseObject(result,
                        TaskBean.class);
                if ("200".equals(bean.getResult())) {

                } else if ("500".equals(bean.getResult())) {
//                    Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
