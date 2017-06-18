package com.yunxi.phone.service;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.StepType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;

import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.greenrobot.event.EventBus;


@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class StepService extends Service implements SensorEventListener {
    private final String TAG = "StepService";
    //默认为10秒进行一次存储
    private static int duration = 10000;
    public static String CURRENTDATE = "";
    private SensorManager sensorManager;
    private StepDcretor stepDetector;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;
    private Messenger messenger = new Messenger(new MessenerHandler());
    private BroadcastReceiver mBatInfoReceiver;
    private WakeLock mWakeLock;
    private TimeCount time;
    public static int CurStep = 0;
    private boolean mIsLogin;

    private SharedPreferences mLoginPreference;
    //测试
//    private static int i = 0;
    private String DB_NAME = "basepedo";

    private static class MessenerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AddressApi.MSG_FROM_CLIENT:
                    try {
                        Messenger messenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null, AddressApi.MSG_FROM_SERVER);
                        Bundle bundle = new Bundle();
                        bundle.putInt("step", StepDcretor.CURRENT_SETP);
                        replyMsg.setData(bundle);
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    AlarmManager am = null;
    @Override
    public void onCreate() {
        super.onCreate();
        initBroadcastReceiver();
        EventBus.getDefault().register(this);
//        am = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent("ELITOR_CLOCK");
//        am.setRepeating(AlarmManager.RTC_WAKEUP,"00:00",am.INTERVAL_DAY,intent);
        new Thread(new Runnable() {
            public void run() {
                startStepDetector();
            }
        }).start();
        startTimeCount();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initTodayData();
        CurStep = StepDcretor.CURRENT_SETP;
        return START_STICKY;
    }

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }


    private void initTodayData() {
        CURRENTDATE = getTodayDate();
//        DbUtils.createDb(this, DB_NAME);
//        //获取当天的数据，用于展示
//        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
////        L.d("StepData"+list.size());
//        if (list.size() == 0 || list.isEmpty()) {
//            StepDcretor.CURRENT_SETP = 0;
//        } else if (list.size() == 1) {
//            StepDcretor.CURRENT_SETP = Integer.parseInt(list.get(0).getStep());
//        } else {
//            Log.v(TAG, "出错了！");
//        }
        try {
            StepData stepData = YunxiApplication.db.selector(StepData.class).where("today", "=", getTodayDate()).findFirst();
            if (stepData == null){
                StepDcretor.CURRENT_SETP = 0;
            }else{
                StepDcretor.CURRENT_SETP=Integer.parseInt(stepData.getStep());
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void initBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //日期修改
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        //日期修改
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        //Intent.ACTION_TIME_CHANGED    日期变更
        //Intent.ACTION_TIME_TICK     时间变更
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //网络变化广播
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.v(TAG, "screen on");
                    save();
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.v(TAG, "screen off");
                    //改为30秒一存储
                    save();
                    duration = 30000;
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    Log.v(TAG, "screen unlock");
                    save();
                    //改为10秒一存储
                    duration = 10000;
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.v(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                    //保存一次
                    save();
                } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                    Log.v(TAG, " receive ACTION_SHUTDOWN");
                    save();
                } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                    Log.v(TAG, " receive ACTION_SHUTDOWN");
                    //网络状态改变 判断是否有未上报的数据 此时上报
//                    String day = (String) SPUtils.get(getApplicationContext(), "today", "");
//                    if (!TextUtils.isEmpty(day)) {
//                        //当不为空时 证明有数据要上报 通过这个时间去获取
//                        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{day});
//                        int stepNumber = 0;
//                        if (list.size() == 1) {
//                            stepNumber = Integer.parseInt(list.get(0).getStep());
//                        }
//                        uploadFailStep(stepNumber, day);
//                    }
                } else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
                    //上报步数
//                    String owner = (String) SPUtils.get(MApplication.context, Consts.OWNER, "0");
//                    String userId = (String) SPUtils.get(MApplication.context, Consts.USER_ID, "0");
//                    if (("1").equals(owner) && userId.equals("0")) {//僵尸模式下  任何必须得登录
//                        //僵尸模式下 不上报
//                    } else {
//                        UploadStep();
//                    }

//                    initTodayData();
//                    clearStepData();
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }



    private void clearStepData() {
//        i = 0;
        StepService.CURRENTDATE = "0";
    }

    private void startTimeCount() {
        time = new TimeCount(duration, 1000);
        time.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private void startStepDetector() {
        if (sensorManager != null && stepDetector != null) {
            sensorManager.unregisterListener(stepDetector);
            sensorManager = null;
            stepDetector = null;
        }
        sensorManager = (SensorManager) this
                .getSystemService(SENSOR_SERVICE);
        getLock(this);
        //android4.4以后可以使用计步传感器
//        int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
//        if (VERSION_CODES >= 19) {
//            addCountStepListener();
//        } else {
//            addBasePedoListener();
//        }

        addBasePedoListener();
        addCountStepListener();
    }

    private void addCountStepListener() {
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (detectorSensor != null) {
            sensorManager.registerListener(StepService.this, detectorSensor, SensorManager.SENSOR_DELAY_UI);
        } else if (countSensor != null) {
            sensorManager.registerListener(StepService.this, countSensor, SensorManager.SENSOR_DELAY_UI);
            //        addBasePedoListener();
        } else {
            Log.v(TAG, "Count sensor not available!");
        }
    }

    private void addBasePedoListener() {
        stepDetector = new StepDcretor(this);
        // 获得传感器的类型，这里获得的类型是加速度传感器
        // 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        Sensor sensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // sensorManager.unregisterListener(stepDetector);
        sensorManager.registerListener(stepDetector, sensor,
                SensorManager.SENSOR_DELAY_UI);
        stepDetector
                .setOnSensorChangeListener(new StepDcretor.OnSensorChangeListener() {

                    @Override
                    public void onChange() {
                        EventBus.getDefault().post(
                                new StepType(StepDcretor.CURRENT_SETP + ""));
                        CurStep = StepDcretor.CURRENT_SETP;
                    }
                });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        i++;
        //   StepDcretor.CURRENT_SETP++;
//        updateNotification(StepDcretor.CURRENT_SETP);
        //更新界面
        EventBus.getDefault().post(
                new StepType(StepDcretor.CURRENT_SETP+""));
        CurStep = StepDcretor.CURRENT_SETP;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 如果计时器正常结束，则开始计步
            time.cancel();
            save();
            startTimeCount();
        }
        @Override
        public void onTick(long millisUntilFinished) {
        }
    }
    private void save() {
//        CURRENTDATE = ;
        try {
        int tempStep = StepDcretor.CURRENT_SETP;
        String user_id = ACache.get(this).getAsString("user_id");
        if(user_id.equals(0)){
            YunxiApplication.db.delete(StepData.class);
            StepDcretor.CURRENT_SETP=0;
            return;
        }
        //-----------------------
            StepData stepData = YunxiApplication.db.selector(StepData.class).where("today", "=", getTodayDate()).findFirst();
            if (stepData == null){
                stepData = new StepData();
                stepData.setToday(getTodayDate());
                stepData.setStep(0 + "");
                YunxiApplication.db.save(stepData);
            }else{
                stepData.setToday(getTodayDate());
                stepData.setStep(tempStep + "");
                YunxiApplication.db.update(stepData);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onDestroy() {
        //取消前台进程
        stopForeground(true);
//        DbUtils.closeDb();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBatInfoReceiver);
//        Intent intent = new Intent(this, StepService.class);
//        startService(intent);
        super.onDestroy();


    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

//    private  void unlock(){
//        setLockPatternEnabled(android.provider.Settings.Secure.LOCK_PATTERN_ENABLED,false);
//    }
//
//    private void setLockPatternEnabled(String systemSettingKey, boolean enabled) {
//        //推荐使用
//        android.provider.Settings.Secure.putInt(getContentResolver(), systemSettingKey,enabled ? 1 : 0);
//    }

    synchronized private WakeLock getLock(Context context) {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld())
                mWakeLock.release();
            mWakeLock = null;
        }

        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
        }
        return (mWakeLock);
    }

    public void onEventMainThread(LogoutCloseType event) {
////        DbUtils.deleteAll(StepData.class);
//        clearStepData();
//        stopSelf();
////        DbUtils.closeDb();
////        StepDcretor.CURRENT_SETP=0;
////        Intent intent = new Intent(getApplication(), StepService.class);
////        stopService(intent);
////        mLoginPreference = getSharedPreferences(LoadingActivity.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
////        mIsLogin = mLoginPreference.getBoolean(LoadingActivity.KEY_IS_LOGIN, false);
////        StepDcretor.CURRENT_SETP=0;

//        StepDcretor.CURRENT_SETP=0;
//        try {
//            StepData stepData = YunxiApplication.db.selector(StepData.class).where("today", "=", getTodayDate()).findFirst();
//            if (stepData != null){
//                StepDcretor.CURRENT_SETP=0;
//                stepData.setToday(getTodayDate());
//                stepData.setStep(0 + "");
//                YunxiApplication.db.update(stepData);
//            }
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
        stopSelf();
    }
}
