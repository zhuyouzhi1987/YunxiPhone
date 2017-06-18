package com.yunxi.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yunxi.phone.service.LockScreenService;


/**
 * 开机广播，启动activity
 */
public class LockScreenReceiver extends BroadcastReceiver {
    private final static String TAG = "LockScreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//            context.startService(new Intent(context, LockScreenService.class));
            Log.i("mylog","开启了锁屏服务");
            Intent i=new Intent(context,LockScreenService.class);
            i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startService(i);

        }
    }

}
