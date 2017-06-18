package com.yunxi.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.bean.MsgBean;
import com.yunxi.phone.bean.Task;
import com.yunxi.phone.eventtype.StepType;
import com.yunxi.phone.service.StepDcretor;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.TextUtil;
import com.yunxi.phone.utils.XUtil;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class BootReceiver extends BroadcastReceiver {
    private final String module = "task/testing";
    private String currentTempFilePath = Environment
            .getExternalStorageDirectory() + "/pf/apps/";

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            EventBus.getDefault().post(
                    new StepType(StepDcretor.CURRENT_SETP + ""));

            try {
                Task task = YunxiApplication.db.selector(Task.class).where("packagename", "=", packageName.replace("package:", "")).findFirst();
                if (task != null) {
//                    goTask(context, task.getAd_id(), 2, task.getTask_id());
                    Intent startIntent = context.getPackageManager().getLaunchIntentForPackage(packageName.replace("package:", ""));
                    File file = new File(currentTempFilePath + TextUtil.getMd5Value(task.getUrl()) + ".apk");
                    if (file.exists()) {
                        file.delete();
                    }
                    if (startIntent != null) {
                        context.startActivity(startIntent);
                        goTask(context, task.getPackagename(), task.getTask_id());
                    } else {
                        Toast.makeText(context, "无法打开应用", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            L.d("安装了:" + packageName + "包名的程序");

        }

        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            EventBus.getDefault().post(
                    new StepType(StepDcretor.CURRENT_SETP + ""));
//            try {
//                Task task = TTHApplication.db.findFirst(Selector.from(Task.class).where("packagename", "=", packageName.replace("package:", "")));
//                if (task != null) {
//                    goTask(context, task.getAd_id(), 4, task.getTask_id());
//                }
//            } catch (DbException e) {
//                e.printStackTrace();
//            }
//            L.d("卸载了:" + packageName + "包名的程序");
        }
    }

    private void goTask(final Context context, String packagename, int task_id) {
        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(context);
        L.d("uid====" + mCache.getAsString("user_id"));
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        map.put("task_id",task_id);
        map.put("package_name",packagename);
        map.put("type",1);
        XUtil.Post(context, AddressApi.SEDN_TASK, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                L.d("SEDN_TASK:" + result);
                MsgBean bean = JSON.parseObject(result,
                        MsgBean.class);
                if ("200".equals(bean.getResult())) {
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(context, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(context, "onCancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }
}
