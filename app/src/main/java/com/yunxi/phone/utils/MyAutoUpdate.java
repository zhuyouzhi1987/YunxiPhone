package com.yunxi.phone.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yunxi.phone.bean.UpdateBean;

import org.xutils.common.Callback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/9.
 */
public class MyAutoUpdate {
    // 调用更新的Activity
    public Activity activity = null;
    // 当前版本号
    public int versionCode = 0;
    // 当前版本名称
    public String versionName = "";
    // 控制台信息标识
    private static final String TAG = "AutoUpdate";
    // 文件当前路径
    private String currentFilePath = Environment.getExternalStorageDirectory()
            + "/yunxi/update/yunxi.apk";
    // 安装包文件临时路径
    private String currentTempFilePath = Environment
            .getExternalStorageDirectory() + "/yunxi/update/";
    String updateVersion;
    private String strURL = "";
    private String updateInfo = "";

    /**
     * 构造方法，获得当前版本信息
     *
     * @param activity
     */
    public MyAutoUpdate(Activity activity) {
        this.activity = activity;
        // 获得当前版本
        getCurrentVersion();
    }

    int version_code = 0;

    /**
     * 检测更新
     */
    public void check(final UpdateCallBack updateCallBack) {
        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(activity);


        map.put("user_id", mCache.getAsString("user_id"));

        map.put("userkey", mCache.getAsString("token"));

        if (!NetUtil.hasNetwork(this.activity)) {
            return;
        }
        XUtil.Post(activity, AddressApi.VERSION, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                UpdateBean bean = JSON.parseObject(result,
                        UpdateBean.class);
                L.d("检查更新--" + result);
                if ("200".equals(bean.getResult())) {
                    version_code = Integer.parseInt(bean.getData().getVersion_code());
                    if (version_code <= versionCode) {
                        if (updateCallBack != null) {
                            updateCallBack.call(true);
                        }
                    } else {
                        strURL = bean.getData().getAndroid_url();
                        updateInfo = bean.getData().getAndroid_info();
                        updateVersion = bean.getData().getAndroid_version();
                        showDialog(strURL, updateInfo, bean.getData().getForce());
                        if (updateCallBack != null) {
                            updateCallBack.call(false);
                        }
                    }
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(activity, bean.getMsg(), Toast.LENGTH_SHORT).show();
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

    boolean tag;

    private void showDialog(final String strURL, String updateInfo, int force) {
        //判断是否下载过
        File myTempFile = new File(currentFilePath);
        if (myTempFile.exists()) {
            tag = true;
        } else {
            tag = false;
        }
        final MyUpdateDialog dialog = new MyUpdateDialog(activity)
                .builder().setMsg(updateInfo)
                .setTitle("版本更新").setNegativeButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //下次再说
                    }
                });
        if (force == 1) {
            dialog.setCancelable(false);
            dialog.setOnTouchOutside(false);
        } else {
            dialog.setCancelable(true);
            dialog.setOnTouchOutside(true);
        }


        dialog.setPositiveButton(tag, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //立即更新 显示进度条
                dialog.setMustVisiable(View.VISIBLE);
                dialog.setNoMustVisiable(View.GONE);
                if (tag) {
                    //打开
                    File myTempFile = new File(currentFilePath);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    // 获得下载好的文件类型
                    String type = getMIMEType(myTempFile);
                    // 打开各种类型文件
                    intent.setDataAndType(Uri.fromFile(myTempFile), type);
                    openFile(myTempFile);
                    dialog.dismiss();
                } else {
                    dialog.setEnable(false);
                    down(strURL, dialog);
                }
            }
        });
        dialog.setNowButton(tag, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //立即更新  显示进度条
                if (tag) {
                    //打开
                    File myTempFile = new File(currentFilePath);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    // 获得下载好的文件类型
                    String type = getMIMEType(myTempFile);
                    // 打开各种类型文件
                    intent.setDataAndType(Uri.fromFile(myTempFile), type);
                    openFile(myTempFile);
                    dialog.dismiss();
                } else {
                    dialog.setEnable(false);
                    down(strURL, dialog);
                }

            }
        });
        if (force == 1) {
            dialog.setMustVisiable(View.VISIBLE);
            dialog.setNoMustVisiable(View.GONE);
        } else {
            dialog.setMustVisiable(View.GONE);
            dialog.setNoMustVisiable(View.VISIBLE);
        }
        dialog.show();
    }

    private void down(String strURL, final MyUpdateDialog dialog) {
//        String url = TextUtil.getMd5Value(strURL) + ".apk";
        File myTempFile = new File(currentFilePath);
        if (myTempFile.exists()) {
            myTempFile.delete();
        }

        XUtil.DownLoadFile(strURL, currentFilePath, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                L.d("avonStartedaa");
                dialog.setProgress(0 + "%");
                dialog.setCancelable(false);
                dialog.setOnTouchOutside(false);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                L.d(total + "------" + current + "aaa");
                int progressTemp = (int) (current * 100 / total);
                dialog.setProgress(progressTemp + "%");
                dialog.setCancelable(false);
                dialog.setOnTouchOutside(false);
            }

            @Override
            public void onSuccess(File result) {
                dialog.setEnable(true);
                L.d(result.getName());
                dialog.setProgress("立即安装");
                File myTempFile = new File(currentFilePath);
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                // 获得下载好的文件类型
                String type = getMIMEType(myTempFile);
                // 打开各种类型文件
                intent.setDataAndType(Uri.fromFile(myTempFile), type);
                tag = true;
                dialog.setCancelable(true);
                dialog.setOnTouchOutside(true);
                openFile(myTempFile);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dialog.setProgress("重新下载");
                dialog.setEnable(true);
                dialog.setCancelable(true);
                dialog.setOnTouchOutside(true);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dialog.setEnable(true);
                L.d("onFinished");
                dialog.setCancelable(true);
                dialog.setOnTouchOutside(true);

            }
        });
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
        activity.startActivity(intent);
    }

    /**
     * 获得当前版本信息
     */
    public void getCurrentVersion() {
        try {
            // 获取应用包信息
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), 0);
            this.versionCode = info.versionCode;
            this.versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
}
