package com.yunxi.phone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.RegisterBean;
import com.yunxi.phone.bean.SendCodeBean;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.CountDownButtonHelperForCode;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.NetUtil;
import com.yunxi.phone.utils.UserInfoUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bond on 16/2/19.
 */
public class FixPwdActivity extends BaseActivity implements View.OnClickListener{

    private AutoRelativeLayout back;

    private EditText phone_et;
    private EditText yanzheng_et;
    private EditText pwd_et;
    private TextView send_code_btn;

    private AutoRelativeLayout fix_btn;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    private AutoLinearLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_fix_pwd;
    }

    @Override
    protected void findById() {
        back=$(R.id.back);
        phone_et=$(R.id.phone_text);
        yanzheng_et=$(R.id.yanzheng_text);
        pwd_et=$(R.id.pwd_text);
        send_code_btn=$(R.id.send_code_txt);
        loading=$(R.id.loading);
        fix_btn=$(R.id.fix_btn);
        not_data=$(R.id.not_data);
        get_data=$(R.id.get_data);

    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        send_code_btn.setOnClickListener(this);
        fix_btn.setOnClickListener(this);
        get_data.setOnClickListener(this);
    }

    @Override
    protected void init() {

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

    private void sendCode(String phone) {


        Map<String, Object> map = new HashMap<>();


        map.put("phone", phone);
        map.put("guid", UUID.randomUUID().toString());


        XUtil.Post(FixPwdActivity.this, AddressApi.VALIDATION_FORGET, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                send_code_btn.setOnClickListener(FixPwdActivity.this);

                L.d("result=" + result);

                SendCodeBean bean = JSON.parseObject(result,
                        SendCodeBean.class);

                if ("200".equals(bean.getResult())) {
                    Toast.makeText(FixPwdActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                    phone_et.setEnabled(false);

                    CountDownButtonHelperForCode helper = new CountDownButtonHelperForCode(
                            send_code_btn, "获取验证", 30, 1,
                            FixPwdActivity.this);

                    helper.setOnFinishListener(new CountDownButtonHelperForCode.OnFinishListener() {

                        @Override
                        public void finish() {
                            phone_et.setEnabled(true);

                        }
                    });

                    helper.start();

                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(FixPwdActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                send_code_btn.setOnClickListener(FixPwdActivity.this);
                Toast.makeText(FixPwdActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });



    }


    private void fixPwd(String phone, String pwd, String code) {
        showLoading();

        Map<String, Object> map = new HashMap<>();


        map.put("phone", phone);
        map.put("newpwd", UserInfoUtil.getMd5Value(pwd.trim()));

        map.put("code", code);


        XUtil.Post(FixPwdActivity.this, AddressApi.FIX_PWD, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                 hideLoading();
                RegisterBean bean = JSON.parseObject(result,
                        RegisterBean.class);
                not_data.setVisibility(View.GONE);
                if ("200".equals(bean.getResult())) {
                    Toast.makeText(FixPwdActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                    FixPwdActivity.this.finish();


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(FixPwdActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
                not_data.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(CancelledException cex) {
                hideLoading();
            }

            @Override
            public void onFinished() {
                hideLoading();
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:

                finish();
                break;
            case R.id.get_data:
                fixPwd(phone_et.getText().toString(), pwd_et.getText().toString(), yanzheng_et.getText().toString());
                break;
            case R.id.send_code_txt:

                char tempTelep[] = phone_et.getText().toString().toCharArray();
                if (!TextUtils.isEmpty(phone_et.getText().toString())
                        && tempTelep.length == 11) {

                    send_code_btn.setOnClickListener(null);

                    sendCode(phone_et.getText().toString());

                } else {
                    phone_et.setError("手机格式错误");
                }


                break;


            case R.id.fix_btn:


                if ("".equals(phone_et.getText().toString())) {
                    Toast.makeText(FixPwdActivity.this, "请填写手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!UserInfoUtil.isMobileNO(phone_et.getText().toString())) {
                    Toast.makeText(FixPwdActivity.this, "手机格式有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(yanzheng_et.getText().toString())) {
                    Toast.makeText(FixPwdActivity.this, "请填写验证码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("".equals(pwd_et.getText().toString())) {
                    Toast.makeText(FixPwdActivity.this, "请填写密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd_et.getText().toString().length() < 6) {
                    Toast.makeText(FixPwdActivity.this, "密码不能小于6位", Toast.LENGTH_SHORT).show();
                    return;
                }

                //去掉自己的图片上传接口
                    fixPwd(phone_et.getText().toString(), pwd_et.getText().toString(), yanzheng_et.getText().toString());
                break;


        }
    }

    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

}
