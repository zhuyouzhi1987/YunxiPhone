package com.yunxi.phone.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.bean.DeviceCodeBean;
import com.yunxi.phone.bean.LockInfo;
import com.yunxi.phone.service.CountDownTimer;
import com.yunxi.phone.service.StepService;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.CallRecordUtils;
import com.yunxi.phone.utils.ContactsManager;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.SPUtils;
import com.yunxi.phone.utils.StatusBarUtil;
import com.yunxi.phone.utils.UserInfoUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bond on 16/2/19.
 */
public class LoadingActivity extends Activity implements View.OnClickListener {

    private boolean mIsLogin;
    private boolean isFirst;

    private SharedPreferences mLoginPreference;
    private SharedPreferences mFirstPreference;

    public static final String LOGIN_PREFERENCE = "login_preferences";

    public static final String KEY_IS_LOGIN = "isLogin";

    public static final int REQ_DEVICE_PERMISSION = 1234;
    private AutoLinearLayout loading;
    private static final Handler sHandler = new Handler();
    private SharedPreferences mSharedPreferences;
    private TextView time;
    RelativeLayout baidussp;
    boolean isPresent=false;
    TextView time_number;
    MyCountDownTimer mc;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsLogin) {
                Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(i);
                LoadingActivity.this.finish();
                //打开计步服务
                Intent intent = new Intent(getApplication(), StepService.class);
                startService(intent);
            } else {
                //去登录页
                Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                i.putExtra("from", "loading");
                startActivity(i);

                LoadingActivity.this.finish();

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        time = (TextView) findViewById(R.id.time_size);
        time_number = (TextView) findViewById(R.id.time_number);
        baidussp = (RelativeLayout) findViewById(R.id.third_ads);


        time.setOnClickListener(LoadingActivity.this);

        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpWhenCanClick(); // 跳转至您的应用主界面
            }
        }, 5000);

        mc = new MyCountDownTimer(5000, 1000);
        mc.start();
        initBaidu();
        StatusBarUtil.setTranslucentForImageView(LoadingActivity.this, 0, null);
        init();

    }

    ACache mCache;
    private void init() {
        mCache = ACache.get(LoadingActivity.this);
//        checkWriteAndPhoneAndLocationPermission();

        //保存默认壁纸
        try {
            LockInfo lockInfo = YunxiApplication.db.selector(LockInfo.class).where("lockId", "=", -1).findFirst();
            if(null==lockInfo){
                lockInfo=new LockInfo();
                lockInfo.setLockId(-1);
                lockInfo.setIsUse("1");
                YunxiApplication.db.save(lockInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if((int) SPUtils.get(getApplicationContext(), "contacts", 0)!=1){
                    //请求数据
                    ContactsManager cm = new ContactsManager(getApplicationContext(), getApplicationContext().getContentResolver());
                    mCache.put("contact", cm.searchContact());
                    SPUtils.put(getApplicationContext(),"contacts",1);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if((int) SPUtils.get(getApplicationContext(), "records", 0)!=1){
                    //通话记录
                    CallRecordUtils callRecord = new CallRecordUtils();
                    String dataList = callRecord.getDataList(LoadingActivity.this);
                    mCache.put("record",dataList);
                    SPUtils.put(getApplicationContext(),"records",1);
                }
            }
        }).start();


        mFirstPreference = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        isFirst = mFirstPreference.getBoolean("isFirst", true);


        mLoginPreference = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        mIsLogin = mLoginPreference.getBoolean(KEY_IS_LOGIN, false);





        if (!"yes".equals(mCache.getAsString("userinfo_first"))) {
            upLoadUserInfo();
        }

    }

    private void initBaidu() {
        // the observer of AD
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdDismissed() {
                Log.i("RSplashActivity", "onAdDismissed");
//                jumpWhenCanClick(); // 跳转至您的应用主界面
            }

            @Override
            public void onAdFailed(String arg0) {
                Log.i("RSplashActivity", "onAdFailed");
                //获取不到广告跳转到主页面

            }

            @Override
            public void onAdPresent() {
                Log.i("RSplashActivity", "onAdPresent");
                //显示跳过按钮
                time.setVisibility(View.VISIBLE);
                time_number.setVisibility(View.GONE);
                isPresent = true;
            }

            @Override
            public void onAdClick() {
                Log.i("RSplashActivity", "onAdClick");
                // 设置开屏可接受点击时，该回调可用
            }
        };
        String adPlaceId = "3316449"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        new SplashAd(this, baidussp, listener, adPlaceId, true);
    }

    public boolean canJumpImmediately = false;

    //每次进入app，调用此接口，只要成功，就不调用了
    private void upLoadUserInfo() {

        Map<String, Object> map = new HashMap<>();
        map.put("imei", UserInfoUtil.getIMEI(LoadingActivity.this));
        map.put("mac", UserInfoUtil.getMacAddress(LoadingActivity.this));
        map.put("channel", UserInfoUtil.getAppMetaData(LoadingActivity.this, "UMENG_CHANNEL"));
        map.put("package", UserInfoUtil.getPackageName(LoadingActivity.this));
        map.put("device", Build.MODEL);
        map.put("imsi", UserInfoUtil.getIMSI(LoadingActivity.this));
        map.put("brand", Build.BRAND);
        map.put("version_app", UserInfoUtil.getAppVersionName(LoadingActivity.this));
        map.put("guid", UUID.randomUUID().toString());
        map.put("timestamp", UserInfoUtil.getTimeStamp());


        XUtil.Post2(AddressApi.GET_USERINFO, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                ACache mCache = ACache.get(LoadingActivity.this);
                mCache.put("userinfo_first", "yes");

                DeviceCodeBean bean = JSON.parseObject(result,
                        DeviceCodeBean.class);

                String resultCode = bean.getResult();

                if ("200".equals(resultCode)) {
                    bean.getData().getEid();
                    mCache.put("eqId", bean.getData().getEid());
                } else if ("500".equals(resultCode)) {
                    Toast.makeText(LoadingActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        sHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        canJumpImmediately = false;
        MobclickAgent.onPause(this);
    }

    private void jumpWhenCanClick() {
        Log.d("test", "this.hasWindowFocus():" + this.hasWindowFocus());
        if (canJumpImmediately) {
//            this.startActivity(new Intent(LoadingActivity.this, BaiduSDKDemo.class));
//            this.finish();
            if(!isFirst) {
                if (mIsLogin) {
                    Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(i);

                    LoadingActivity.this.finish();
                    //打开计步服务
                    Intent intent = new Intent(getApplication(), StepService.class);
                    startService(intent);
                } else {
                    //去登录页
                    Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                    i.putExtra("from", "loading");
                    startActivity(i);
                    LoadingActivity.this.finish();
                    L.d("jumpWhenCanClick loading");
                }
            }else{
                Intent i = new Intent(LoadingActivity.this, GuideAcitivity.class);
                startActivity(i);
                LoadingActivity.this.finish();
                mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putBoolean("isFirst", false);
                e.commit();
            }
        } else {
            canJumpImmediately = true;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_size:
                if(!isFirst) {
                    if (mIsLogin) {
                        Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(i);

                        LoadingActivity.this.finish();
                        //打开计步服务
                        Intent intent = new Intent(getApplication(), StepService.class);
                        startService(intent);
                    } else {
                        //去登录页
                        Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                        i.putExtra("from", "loading");
                        startActivity(i);

                        LoadingActivity.this.finish();

                    }
                }else{
                    Intent i = new Intent(LoadingActivity.this, GuideAcitivity.class);
                    startActivity(i);
                    mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = mSharedPreferences.edit();

                    e.putBoolean("isFirst", false);
                    e.commit();
                    LoadingActivity.this.finish();
                }


                break;
        }
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
        canJumpImmediately=false;
        if(!isFirst) {
            if (mIsLogin) {
                Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(i);

                LoadingActivity.this.finish();
                //打开计步服务
                Intent intent = new Intent(getApplication(), StepService.class);
                startService(intent);
            } else {
                //去登录页
                Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                i.putExtra("from", "loading");
                L.d("jump loading");
                startActivity(i);

                LoadingActivity.this.finish();

            }
        }else{
            Intent i = new Intent(LoadingActivity.this, GuideAcitivity.class);
            startActivity(i);
            mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putBoolean("isFirst", false);
            e.commit();
            LoadingActivity.this.finish();
        }
    }

    private void checkWriteAndPhoneAndLocationPermission() {
        // 定位精确位置
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            YunxiApplication.READ_PHONE_STATE = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_PHONE_STATE);
            YunxiApplication.WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            YunxiApplication.ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            if (YunxiApplication.READ_PHONE_STATE == -1 || YunxiApplication.WRITE_EXTERNAL_STORAGE == -1 || YunxiApplication.ACCESS_COARSE_LOCATION == -1) {//如果其中一个为-1,则需要申请权限
                if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                    permissionsList.add(Manifest.permission.READ_PHONE_STATE);
                if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                    permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
                    permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (permissionsList.size() != 0) {
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQ_DEVICE_PERMISSION);
                } else {
                    //已经不是第一次,已经有权限
//                    L.d("test", "permissionsList.size()==0");
                }
            }
        }
    }



    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p/>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p/>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {

        }

        public void onTick(long millisUntilFinished) {
            time_number.setText(millisUntilFinished / 1000+1 + "s");
            if(millisUntilFinished / 1000<3&&!isPresent){
                if (mc != null) {
                    mc.cancel();
                    mc = null;
                }
                jump();
                time_number.setVisibility(View.GONE);
            }
        }
    }

    private class MyTask extends AsyncTask<String, Integer, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            ContactsManager cm = new ContactsManager(getApplicationContext(), getApplicationContext().getContentResolver());
            mCache.put("contact", cm.searchContact());
            //通话记录
            CallRecordUtils callRecord = new CallRecordUtils();
            String dataList = callRecord.getDataList(LoadingActivity.this);
            mCache.put("record",dataList);
            SPUtils.put(getApplicationContext(), "contacts", 1);
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
}
