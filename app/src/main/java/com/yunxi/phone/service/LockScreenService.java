package com.yunxi.phone.service;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.yunxi.phone.activity.LockActivity;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.utils.L;


public class LockScreenService extends Service {
    private final static String TAG = "LockScreenService";
    public static LockScreenService service;
    private Context mContext;
    boolean flag = false;
    public static KeyguardManager.KeyguardLock keyguardLock;
    NotificationCompat.Builder ncb;
    Notification noti;
    NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void unlock() {
        keyguardLock.reenableKeyguard();
    }

    public void lock() {
        keyguardLock.disableKeyguard();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(getApplicationContext().KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("");
        service = this;
        L.d("关闭系统锁屏");
        keyguardLock.disableKeyguard();


        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mContext = getApplicationContext();
        //注册广播
        IntentFilter mScreenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mScreenOffFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenOffFilter.addAction("lock");
        mScreenOffFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        LockScreenService.this.registerReceiver(mScreenOffReceiver, mScreenOffFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LockScreenService.this.unregisterReceiver(mScreenOffReceiver);
        //重新启动activity
        startService(new Intent(LockScreenService.this, LockScreenService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    /**
     * 屏幕变亮的广播，这里要隐藏系统的锁屏界面
     */
    private  BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {



            //如果是来电
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    L.d("来电");
                    flag = true;
                    if (LockActivity.activity != null) {
                        LockActivity.activity.finish();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    L.d("接听");
                    flag = true;
                    if (LockActivity.activity != null) {
                        LockActivity.activity.finish();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    L.d("CALL_STATE_IDLE");
                    flag = false;

                    break;
            }
            if (flag) {
                return;
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals("lock")) {
                        if (keyguardLock != null) {
                            //关闭系统锁屏
                            keyguardLock.disableKeyguard();
                        }


                L.d("此处应该执行锁屏");
                Intent lockIntent = new Intent(getApplicationContext(), LockActivity.class);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(lockIntent);
//                ControlService.selfAction = true;
//                stopService(new Intent(LockScreenService.this, ControlService.class));
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//                ControlService.selfAction = false;
//                startService(new Intent(LockScreenService.this, ControlService.class));
            }
        }
    };
}
