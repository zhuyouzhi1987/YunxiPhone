package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.RegisterBean;
import com.yunxi.phone.eventtype.RefershType;
import com.yunxi.phone.service.StepService;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.MyAutoUpdate;
import com.yunxi.phone.utils.SPUtils;
import com.yunxi.phone.utils.UpdateCallBack;
import com.yunxi.phone.utils.UserInfoUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by bond on 16/2/19.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private AutoRelativeLayout back;
    private AutoRelativeLayout reg_btn;
    private TextView forget_btn;

    private AutoRelativeLayout login_btn;

    private EditText phone_et, pwd_et;

    private SharedPreferences mSharedPreferences;
    private static final String LOGIN_PREFERENCE = "login_preferences";
    private TextView tv_desc;
    private String from;
    private static final int MSG_SET_ALIAS = 1001;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        reg_btn = $(R.id.reg_btn);
        forget_btn = $(R.id.forget_pwd_btn);
        login_btn = $(R.id.login_btn);
        phone_et = $(R.id.phone_text);
        pwd_et = $(R.id.pwd_text);
        loading = $(R.id.loading);
        not_data = $(R.id.not_data);
        get_data = $(R.id.get_data);
        tv_desc = $(R.id.tv_desc);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        reg_btn.setOnClickListener(this);
        forget_btn.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        get_data.setOnClickListener(this);
    }

    @Override
    protected void init() {

        from = getIntent().getStringExtra("from");

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE,
                Context.MODE_PRIVATE);
        initUpdate();
    }


    private void login(String phone, String pwd) {
        showLoading();
        tv_desc.setText("正在登录...");
        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(LoginActivity.this);
        map.put("eq_id", mCache.getAsString("eqId"));
        map.put("phone", phone);
        map.put("password", UserInfoUtil.getMd5Value(pwd.trim()));
        map.put("guid", UUID.randomUUID().toString());


        XUtil.Post(LoginActivity.this, AddressApi.LOGIN, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                hideLoading();
                RegisterBean bean = JSON.parseObject(result,
                        RegisterBean.class);
                not_data.setVisibility(View.GONE);
                if ("200".equals(bean.getResult())) {
                    //设置别名

                    Intent service = new Intent(getApplication(), StepService.class);
                    startService(service);
                    Toast.makeText(LoginActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                    ACache mCache = ACache.get(LoginActivity.this);
                    mCache.put("user_id", bean.getData().getUser_id());
                    mCache.put("token", bean.getData().getUserkey());
                    mCache.put("check", String.valueOf(bean.getData().getCheck()));
                    mCache.put("phone", phone_et.getText().toString());

                    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, bean.getData().getUser_id()));
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean("isLogin", true);
                    e.commit();


                    if ("loading".equals(from)) {

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);


                        LoginActivity.this.finish();

                    } else if ("app_in".equals(from)) {

                        EventBus.getDefault().post(
                                new RefershType(""));

                        LoginActivity.this.finish();
                    }


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(LoginActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
                not_data.setVisibility(View.VISIBLE);
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
        super.onDestroy();
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
        switch (view.getId()) {
            case R.id.back:

                finish();

                openActivity(MainActivity.class);

                break;

            case R.id.reg_btn:

                openActivity(RegisterActivity.class);

                break;

            case R.id.forget_pwd_btn:
                openActivity(FixPwdActivity.class);
                break;

            case R.id.login_btn:


                if (!TextUtils.isEmpty(phone_et.getText().toString()) && !TextUtils.isEmpty(pwd_et.getText().toString())) {
                    login(phone_et.getText().toString(), pwd_et.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "手机号和密码不能为空！", Toast.LENGTH_SHORT).show();
                    login_btn.setOnClickListener(this);
                }

                break;
            case R.id.get_data:
                login(phone_et.getText().toString(), pwd_et.getText().toString());
                break;

        }
    }

    @Override
    public void onBackPressed() {

        finish();

        openActivity(MainActivity.class);

    }


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

    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    private void initUpdate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String format = df.format(new Date());
        if (!SPUtils.get(getApplicationContext(), "update", "0").equals(format)) {
            MyAutoUpdate update = new MyAutoUpdate(this);
            update.check(new UpdateCallBack() {
                @Override
                public void call(boolean flag) {

                }
            });
            SPUtils.put(getApplicationContext(), "update", format);
        }
    }

}
