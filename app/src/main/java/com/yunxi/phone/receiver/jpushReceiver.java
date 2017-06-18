package com.yunxi.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunxi.phone.activity.SystemMsgActivity;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.eventtype.RefershType;
import com.yunxi.phone.service.StepData;
import com.yunxi.phone.service.StepDcretor;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.L;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/12.
 */
public class jpushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            L.d("[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            L.d("[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//            processCustomMessage(context, bundle);
            String extra1 = bundle.getString(JPushInterface.EXTRA_MESSAGE);


            try {
                JSONObject jsonObj = JSON.parseObject(extra1);

                if (jsonObj != null && !jsonObj.isEmpty()) {
                    String type = jsonObj.getString("type");
                    String data=jsonObj.getString("data");

                    switch (Integer.parseInt(type)) {
                        case 1:

                            StepData stepData = YunxiApplication.db.selector(StepData.class).where("today", "=", data).findFirst();
                            if (stepData != null) {
                                StepDcretor.CURRENT_SETP = 0;
                                stepData.setToday(data);
                                stepData.setStep(0 + "");
                                YunxiApplication.db.update(stepData);
                            }
                            L.d("StepDcretor.CURRENT_SETP:   +"+StepDcretor.CURRENT_SETP);
                            EventBus.getDefault().post(
                                    new RefershType(""));
                            break;
                        case 2:

                            EventBus.getDefault().post(
                                            new RefershType(""));
                            break;

                        default:
                            break;

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            L.d("[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            L.d("[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            //接收到系统消息  添加未读数
            ACache mCache = ACache.get(context);
            mCache.put("systemNoRead", "1");


        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            L.d("[MyReceiver] 用户点击打开了通知");
            OpenNotifation(context);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            L.d("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            L.d("[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            L.d("[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void OpenNotifation(Context context) {
        Intent intent = new Intent(context, SystemMsgActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
    }
}
