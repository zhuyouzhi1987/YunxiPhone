package com.yunxi.phone.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.yunxi.phone.marsdaemon.DaemonClient;
import com.yunxi.phone.marsdaemon.DaemonConfigurations;
import com.yunxi.phone.receiver.LockScreenReceiver;
import com.yunxi.phone.receiver.Receiver2;
import com.yunxi.phone.service.LockScreenService;
import com.yunxi.phone.service.Service2;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.SPUtils;

import org.xutils.DbManager;
import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by bond on 2016/12/26.
 */

public class YunxiApplication extends Application {

    private static YunxiApplication instance;

    private DbManager.DaoConfig daoConfig;
    public static DbManager db;
    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }
    //锁屏开关

    private DaemonClient mDaemonClient;


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

        x.Ext.init(this);

        String appId = "37bb2258b5"; // 上Bugly(bugly.qq.com)注册产品获取的AppId

        boolean isDebug = false; // true代表App处于调试阶段，false代表App发布阶段

        CrashReport.initCrashReport(this, appId, isDebug); // 初始化SDK


        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //TbsDownloader.needDownload(getApplicationContext(), false);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                L.d(" onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub

            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                L.d( "onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                L.d( "onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                L.d("onDownloadProgress:" + i);
            }
        });

        QbSdk.initX5Environment(getApplicationContext(), cb);

        initDb();
        initUmeng();
        initJpush();
        initService();
    }



    private void initService() {
        startService(new Intent(getApplicationContext(), LockScreenService.class));
        startService(new Intent(getApplicationContext(), Service2.class));

        L.d("开启锁屏服务");
    }

    private void initJpush() {
        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }

    private void initUmeng() {
        UMShareAPI.get(this);
        PlatformConfig.setWeixin(AddressApi.WEICHAT_APP_ID, AddressApi.WEICHAT_APP_KEY);
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo(AddressApi.SINA_APP_ID, AddressApi.SINA_APP_KEY);
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone(AddressApi.QQ_APP_ID, AddressApi.QQ_APP_KEY);
        Config.DEBUG = false;
        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
        MobclickAgent.openActivityDurationTrack(false);
    }

    private void initDb() {
        //初始化数据库 by bond
        x.Ext.init(this);
        daoConfig = new DbManager.DaoConfig()
                .setDbName("yunxi_db")
                .setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {

                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //未来对数据库升级时使用
                    }
                });

        db = x.getDb(daoConfig);

    }


    public static YunxiApplication getInstance() {
        return instance;
    }

    private String mVersionName = null;

    public String getVersionName() {
        try {
            if (mVersionName == null) {
                final PackageInfo apk = getPackageManager().getPackageInfo(
                        getPackageName(), 0);
                mVersionName = apk.versionName;
            }
            return mVersionName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
    }

    /**
     * Android6.0权限申请状态   0为申请了   -1为未申请
     */
    public static int CAMREA_PERMISSION = -1;
    public static int WRITE_EXTERNAL_STORAGE = -1;
    public static int READ_PHONE_STATE = -1;
    public static int ACCESS_COARSE_LOCATION = -1;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mDaemonClient = new DaemonClient(createDaemonConfigurations());
        mDaemonClient.onAttachBaseContext(base);
    }


    private DaemonConfigurations createDaemonConfigurations(){
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.yunxi.phone:process1",
                LockScreenService.class.getCanonicalName(),
                LockScreenReceiver.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.yunxi.phone:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());
        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }


    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }

}
