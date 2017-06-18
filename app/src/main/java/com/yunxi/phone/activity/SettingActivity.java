package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.YanZhengType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.DataCleanManager;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.MyAutoUpdate;
import com.yunxi.phone.utils.UpdateCallBack;
import com.zhy.autolayout.AutoRelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by bond on 16/2/19.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private AutoRelativeLayout back;
    private ImageView red_icon;

    private TextView cache_tv;

    private String size;

    private TextView phone_tv;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    private AutoRelativeLayout system_message_layout,about_layout,feedback_layout,cache_layout,update_layout,logout_layout,my_account_layout,my_lock_layout;
    private AutoRelativeLayout my_collection_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void findById() {
        back=$(R.id.back);
        system_message_layout=$(R.id.system_message_layout);
        about_layout=$(R.id.about_layout);
        feedback_layout=$(R.id.feedback_layout);
        cache_layout=$(R.id.cache_layout);
        update_layout=$(R.id.update_layout);
        logout_layout=$(R.id.logout_layout);
        cache_tv=$(R.id.cache_size);
        phone_tv=$(R.id.phone);
        red_icon=$(R.id.red_icon);
        my_account_layout=$(R.id.my_account_layout);
        my_collection_layout=$(R.id.my_collection_layout);
        my_lock_layout=$(R.id.my_lock_layout);

    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        system_message_layout.setOnClickListener(this);
        about_layout.setOnClickListener(this);
        feedback_layout.setOnClickListener(this);
        cache_layout.setOnClickListener(this);
        update_layout.setOnClickListener(this);
        logout_layout.setOnClickListener(this);
        my_account_layout.setOnClickListener(this);
        my_collection_layout.setOnClickListener(this);
        my_lock_layout.setOnClickListener(this);
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        try {
            size= DataCleanManager.getTotalCacheSize(SettingActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cache_tv.setText(size);


        ACache cache=ACache.get(SettingActivity.this);
        String check=cache.getAsString("check");

        if("0".equals(check)){
            phone_tv.setText("验证领取100云朵");
        }else if("1".equals(check)) {
            phone_tv.setText("已验证");
            my_account_layout.setOnClickListener(null);
        }


        //判断是否有未读消息
        ACache mCache = ACache.get(SettingActivity.this);
        String noRead = mCache.getAsString("systemNoRead");
        if(null!=noRead){
            if(noRead.equals("1")){
                //有未读
                red_icon.setVisibility(View.VISIBLE);
            }else{
                red_icon.setVisibility(View.GONE);
            }
        }
    }


    private void showWarnDialog(){
        MyAlertDialog dialog = new MyAlertDialog(SettingActivity.this)
                .builder().setMsg("退出登录后将无法免费通话 , 是否继续 ?")
                .setTitle("系统提示")
                .setNegativeButton("暂不退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.setPositiveButton("确认退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout();

            }
        });

        dialog.show();
    }


    private void logout(){
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.putBoolean(KEY_IS_LOGIN, false);
        e.commit();

        ACache mCache = ACache.get(SettingActivity.this);
        mCache.put("user_id", "0");
        mCache.put("token", "0");
        mCache.put("check","0");
        mCache.put("phone","");


        Intent intent = new Intent(SettingActivity.this,
                LoginActivity.class);
        intent.putExtra("from","loading");
        startActivity(intent);


        EventBus.getDefault().post(
                new LogoutCloseType("close"));

        SettingActivity.this.finish();
        //激光注销掉
        JPushInterface.setAlias(getApplicationContext(),"0",mAliasCallback);

    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);

                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

        }

    };
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };



    public void onEventMainThread(YanZhengType event) {

        phone_tv.setText("已验证");
        my_account_layout.setOnClickListener(null);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:

            finish();

                break;

            case R.id.system_message_layout:

                Intent msgIntent=new Intent(SettingActivity.this,SystemMsgActivity.class);
                startActivityForResult(msgIntent,100);
                break;
            case R.id.about_layout:

                Intent i=new Intent(SettingActivity.this,AboutActivity.class);
                startActivity(i);

                break;
            case R.id.feedback_layout:

                Intent fedIntent=new Intent(SettingActivity.this,FedbackActivity.class);
                startActivity(fedIntent);
                break;
            case R.id.cache_layout:

                if("0.0KB".equals(cache_tv.getText().toString())){

                    Toast.makeText(SettingActivity.this,"没有缓存可清除",Toast.LENGTH_SHORT).show();

                }else{

                    DataCleanManager.clearAllCache(SettingActivity.this);

                    try {
                        size=DataCleanManager.getTotalCacheSize(SettingActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    cache_tv.setText(size);

                    Toast.makeText(SettingActivity.this,"清除缓存成功",Toast.LENGTH_SHORT).show();

                }


                break;

            case R.id.my_collection_layout:

                Intent collection = new Intent(this,MyCollectionActivity.class);
                startActivity(collection);

                break;

            case R.id.update_layout:
                initUpdate();


                break;
            case R.id.logout_layout:

                showWarnDialog();
                break;
            case R.id.my_account_layout:
                Intent intent = new Intent(this,CertificationActivity.class);
                startActivity(intent);
                break;
            case R.id.my_lock_layout:
                Intent lockIntent = new Intent(this,LockSettingActivity.class);
                startActivity(lockIntent);
                break;

        }
    }

    private void initUpdate() {
            MyAutoUpdate update = new MyAutoUpdate(this);
            update.check(new UpdateCallBack() {
                @Override
                public void call(boolean flag) {
                    if (flag) {
                        Toast.makeText(getApplicationContext(),"已是最新版本",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            red_icon.setVisibility(View.GONE);
        }
    }
}
