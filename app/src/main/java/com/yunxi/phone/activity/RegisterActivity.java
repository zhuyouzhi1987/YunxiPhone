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
import com.yunxi.phone.eventtype.ChangeTabType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.CountDownButtonHelperForCode;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.NetUtil;
import com.yunxi.phone.utils.UserInfoUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by bond on 16/2/19.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private AutoRelativeLayout back;

    private EditText phone_et;
    private EditText yanzheng_et;
    private EditText pwd_et;
    private TextView send_code_btn;
    private EditText invate_et;

    private AutoRelativeLayout reg_btn;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;

    private ImageView p_btn;
    private TextView p_page_btn;

    private boolean isSelectP=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        phone_et = $(R.id.phone_text);
        yanzheng_et = $(R.id.yanzheng_text);
        pwd_et = $(R.id.pwd_text);
        send_code_btn = $(R.id.send_code_txt);
        invate_et=$(R.id.select_text);

        p_btn=$(R.id.p_btn);
        p_page_btn=$(R.id.p_page_btn);

        reg_btn = $(R.id.reg_btn);
        loading=$(R.id.loading);
        not_data=$(R.id.not_data);
        get_data=$(R.id.get_data);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        send_code_btn.setOnClickListener(this);
        reg_btn.setOnClickListener(this);
        get_data.setOnClickListener(this);
        p_btn.setOnClickListener(this);
        p_page_btn.setOnClickListener(this);
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


        ACache mCache = ACache.get(RegisterActivity.this);

        map.put("eq_id", mCache.getAsString("eqId"));

        map.put("phone", phone);
        map.put("Type", 0);
        map.put("guid", UUID.randomUUID().toString());


        XUtil.Post(RegisterActivity.this, AddressApi.VALIDATION, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                send_code_btn.setOnClickListener(RegisterActivity.this);

                SendCodeBean bean = JSON.parseObject(result,
                        SendCodeBean.class);

                if ("200".equals(bean.getResult())) {
                    Toast.makeText(RegisterActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                    phone_et.setEnabled(false);

                    CountDownButtonHelperForCode helper = new CountDownButtonHelperForCode(
                            send_code_btn, "获取验证", 30, 1,
                            RegisterActivity.this);

                    helper.setOnFinishListener(new CountDownButtonHelperForCode.OnFinishListener() {

                        @Override
                        public void finish() {
                            phone_et.setEnabled(true);

                        }
                    });

                    helper.start();

                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(RegisterActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                send_code_btn.setOnClickListener(RegisterActivity.this);
                Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }


    private void register(String phone, String pwd, String invate_code, String code) {

        showLoading();
        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(RegisterActivity.this);

        map.put("eq_id", mCache.getAsString("eqId"));
        map.put("phone", phone);
        map.put("password", UserInfoUtil.getMd5Value(pwd.trim()));
        if ("".equals(invate_code)) {
            map.put("invitecode", "");
        } else {
            map.put("invitecode", invate_code);
        }
        map.put("code", code);


        map.put("guid", UUID.randomUUID().toString());


        XUtil.Post(RegisterActivity.this, AddressApi.REGISTER, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                hideLoading();
                RegisterBean bean = JSON.parseObject(result,
                        RegisterBean.class);

                if ("200".equals(bean.getResult())) {
                    Toast.makeText(RegisterActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                    RegisterActivity.this.finish();

                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(RegisterActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
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


    private void showWarnDialog(){
        MyAlertDialog dialog = new MyAlertDialog(RegisterActivity.this)
                .builder().setMsg("本号码将作为您呼出电话时的验证依据 , 并可能为被叫的来电显示 , 是否继续验证 ?")
                .setTitle("系统提示")
                .setNegativeButton("暂不验证", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.setPositiveButton("继续验证", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                char tempTelep[] = phone_et.getText().toString().toCharArray();
                if (!TextUtils.isEmpty(phone_et.getText().toString())
                        && tempTelep.length == 11) {

                    send_code_btn.setOnClickListener(null);

                    sendCode(phone_et.getText().toString());

                } else {
                    phone_et.setError("手机格式错误");
                }


            }
        });

        dialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:

                finish();

                break;
            case R.id.get_data:
                register(phone_et.getText().toString(), pwd_et.getText().toString(), invate_et.getText().toString(), yanzheng_et.getText().toString());

                break;

            case R.id.send_code_txt:

                if("".equals(phone_et.getText().toString())){

                    Toast.makeText(RegisterActivity.this,"请填写手机号",Toast.LENGTH_SHORT).show();

                }else{
                    showWarnDialog();
                }





                break;


            case R.id.reg_btn:


                if ("".equals(phone_et.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "请填写手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!UserInfoUtil.isMobileNO(phone_et.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "手机格式有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(yanzheng_et.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "请填写验证码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("".equals(pwd_et.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "请填写密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd_et.getText().toString().length() < 6) {
                    Toast.makeText(RegisterActivity.this, "密码不能小于6位", Toast.LENGTH_SHORT).show();
                    return;
                }



                   if(phone_et.getText().toString().startsWith("14") || phone_et.getText().toString().startsWith("17")){

                        showDialog();

                   }else{

                       if(isSelectP){
                           register(phone_et.getText().toString(), pwd_et.getText().toString(), invate_et.getText().toString(), yanzheng_et.getText().toString());
                       }else{
                           Toast.makeText(RegisterActivity.this,"请勾选协议",Toast.LENGTH_SHORT).show();
                       }


                   }

                break;

            case R.id.p_btn:

                if(isSelectP){

                    p_btn.setImageResource(R.mipmap.p_unpress);
                    isSelectP=false;

                }else{

                    p_btn.setImageResource(R.mipmap.p_press);
                    isSelectP=true;

                }


                break;

            case R.id.p_page_btn:

                Intent p =  new Intent(RegisterActivity.this,ProtocolActivity.class);
                startActivity(p);

                break;

        }
    }

    private void showDialog() {
        MyAlertDialog dialog = new MyAlertDialog(RegisterActivity.this)
                .builder().setMsg("暂不支持14/17开头的手机号使用电话服务 , 但不影响积分兑换 , 是否继续注册 ?")
                .setTitle("系统提示")
                .setNegativeButton("稍后再说", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        dialog.setPositiveButton("继续注册", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register(phone_et.getText().toString(), pwd_et.getText().toString(), invate_et.getText().toString(), yanzheng_et.getText().toString());

            }
        });

        dialog.show();
    }


    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}
