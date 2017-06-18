package com.yunxi.phone.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.activity.LoginActivity;
import com.yunxi.phone.bean.ExchangeBean;
import com.yunxi.phone.bean.ExchangeDataBean;
import com.yunxi.phone.bean.MsgBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.RefershType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/19.
 */
public class QbFragment extends Fragment implements View.OnClickListener {
    AutoRelativeLayout login_btn;
    EditText phone_text;
    EditText pwd_text;
    TextView tv;
    TextView tv_send;
    TextView tv_one;
    TextView tv_two;
    TextView tv_three;
    AutoRelativeLayout rl_one;
    AutoRelativeLayout rl_two;
    AutoRelativeLayout rl_three;
    ExchangeBean bean;
    int money = 0;//可兑换金额
    int clickTab = 1;
    ExchangeDataBean data;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.qb_tab_layout, container, false);

        phone_text = (EditText) view.findViewById(R.id.phone_text);
        pwd_text = (EditText) view.findViewById(R.id.pwd_text);
        login_btn = (AutoRelativeLayout) view.findViewById(R.id.login_btn);
        tv_send = (TextView) view.findViewById(R.id.tv_send);

        rl_one = (AutoRelativeLayout) view.findViewById(R.id.rl_one);
        rl_two = (AutoRelativeLayout) view.findViewById(R.id.rl_two);
        rl_three = (AutoRelativeLayout) view.findViewById(R.id.rl_three);

        tv_one = (TextView) view.findViewById(R.id.tv_one);
        tv_two = (TextView) view.findViewById(R.id.tv_two);
        tv_three = (TextView) view.findViewById(R.id.tv_three);

        loading = (AutoLinearLayout) view.findViewById(R.id.loading);
        not_data = (AutoRelativeLayout) view.findViewById(R.id.not_data);
        get_data = (AutoRelativeLayout) view.findViewById(R.id.get_data);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            getData();
    }

    private void getData() {
        showLoading();
        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(getActivity());

        if ("".equals(mCache.getAsString("user_id")) || mCache.getAsString("user_id") == null) {
            map.put("user_id", "0");
        } else {
            map.put("user_id", mCache.getAsString("user_id"));
        }

        if ("".equals(mCache.getAsString("token")) || mCache.getAsString("token") == null) {
            map.put("userkey", "0");
        } else {
            map.put("userkey", mCache.getAsString("token"));
        }

//        map.put("page", pager);

        XUtil.Post(getActivity(), AddressApi.GET_EXCHANGE, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                L.d(result.toString());
                hideLoading();
                bean = JSON.parseObject(result,
                        ExchangeBean.class);
                not_data.setVisibility(View.GONE);
                if ("200".equals(bean.getResult())) {
                    //加载头部数据
                    data = bean.getData();
                    if (null != data) {

                        initView();
                    }


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
                } else if ("300".equals(bean.getResult())) {

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(getActivity());

                    String phone = mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check", "0");
                    mCache.put("phone", "");

                    //激光注销掉
                    JPushInterface.setAlias(getActivity(), "0", new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });


                    showWarnDialog(phone);


                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                L.d("异常" + ex.toString());
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

    private void showWarnDialog(String phone){
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("您的账号"+phone+"已再其他设备登录 , 如非本人操作请重置密码 ! 确保账号安全 ！")
                .setTitle("系统提示")
                .setNegativeButton("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.setPositiveButton("重新登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent login = new Intent(getActivity(), LoginActivity.class);
                login.putExtra("from", "loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }


    private void initView() {
        tv_one.setText(data.getDoorsill().get(0).getDoorsill() + "个");
        tv_two.setText(data.getDoorsill().get(1).getDoorsill() + "个");
        tv_three.setText(data.getDoorsill().get(2).getDoorsill() + "个");


        money = Integer.parseInt(data.getIntegral()) / data.getProportion().getCloudNumber();
        L.d(money + ".......");


//        if(money<bean.getDoorsill().get(0).getDoorsill()){
//            login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
//            login_btn.setEnabled(false);
//            tv_send.setText("余额不足");
//        }else{
//            login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
//            login_btn.setEnabled(true);
//            tv_send.setText("提交申请");
//        }
    }

    private void init() {
        login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
        login_btn.setEnabled(false);
        login_btn.setOnClickListener(this);
        initTab();
        rl_one.setOnClickListener(this);
        rl_two.setOnClickListener(this);
        rl_three.setOnClickListener(this);
        get_data.setOnClickListener(this);
        phone_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (s.toString().equals(pwd_text.getText().toString())) {
                        switch (clickTab) {
                            case 1:
                                if (money <= data.getDoorsill().get(0).getDoorsill()) {
                                    login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                                    login_btn.setEnabled(false);
                                    tv_send.setText("余额不足");
                                } else {
                                    login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                                    login_btn.setEnabled(true);
                                    tv_send.setText("提交申请");
                                }
                                break;
                            case 2:
                                if (money <= data.getDoorsill().get(1).getDoorsill()) {
                                    login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                                    login_btn.setEnabled(false);
                                    tv_send.setText("余额不足");
                                } else {
                                    login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                                    login_btn.setEnabled(true);
                                    tv_send.setText("提交申请");
                                }
                                break;
                            case 3:
                                if (money <= data.getDoorsill().get(2).getDoorsill()) {
                                    login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                                    login_btn.setEnabled(false);
                                    tv_send.setText("余额不足");
                                } else {
                                    login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                                    login_btn.setEnabled(true);
                                    tv_send.setText("提交申请");
                                }
                                break;
                        }

                    } else {
                        login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                        login_btn.setEnabled(false);
                    }
                } else {
                    login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                    login_btn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pwd_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
//                    if (s.toString().equals(phone_text.getText().toString())) {
                    switch (clickTab) {
                        case 1:
                            if (money <= data.getDoorsill().get(0).getDoorsill()) {
                                login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                                login_btn.setEnabled(false);
                                tv_send.setText("余额不足");
                            } else {
                                login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                                login_btn.setEnabled(true);
                                tv_send.setText("提交申请");
                            }
                            break;
                        case 2:
                            if (money <= data.getDoorsill().get(1).getDoorsill()) {
                                login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                                login_btn.setEnabled(false);
                                tv_send.setText("余额不足");
                            } else {
                                login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                                login_btn.setEnabled(true);
                                tv_send.setText("提交申请");
                            }
                            break;
                        case 3:
                            if (money <= data.getDoorsill().get(2).getDoorsill()) {
                                login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                                login_btn.setEnabled(false);
                                tv_send.setText("余额不足");
                            } else {
                                login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                                login_btn.setEnabled(true);
                                tv_send.setText("提交申请");
                            }
                            break;
                    }
//                    } else {
//                        login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
//                        login_btn.setEnabled(false);
//                    }
                } else {
                    login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                    login_btn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initTab() {
        tv_one.setTextColor(Color.parseColor("#00CBAE"));
        tv_two.setTextColor(Color.parseColor("#999999"));
        tv_three.setTextColor(Color.parseColor("#999999"));
        rl_one.setBackgroundResource(R.drawable.green_frame);
        rl_two.setBackgroundResource(R.drawable.default_frame);
        rl_three.setBackgroundResource(R.drawable.default_frame);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:

                if(!pwd_text.getText().toString().equals(phone_text.getText().toString())){
                    Toast.makeText(getActivity(),"两次输入的号码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }
                send();
                break;
            case R.id.rl_one:
                clickTab1Layout();
                break;
            case R.id.rl_two:
                clickTab2Layout();
                break;
            case R.id.rl_three:
                clickTab3Layout();
                break;
            case R.id.get_data:
                    getData();
                break;

        }
    }

    private void send() {
        login_btn.setEnabled(false);
        showLoading();
        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(getActivity());

        if ("".equals(mCache.getAsString("user_id")) || mCache.getAsString("user_id") == null) {
            map.put("user_id", "0");
        } else {
            map.put("user_id", mCache.getAsString("user_id"));
        }

        if ("".equals(mCache.getAsString("token")) || mCache.getAsString("token") == null) {
            map.put("userkey", "0");
        } else {
            map.put("userkey", mCache.getAsString("token"));
        }
        map.put("Number", phone_text.getText().toString());
        map.put("Doorsill", data.getDoorsill().get(clickTab - 1).getDoorsill() + "");
        map.put("Type", 2);
        XUtil.Post(getActivity(), AddressApi.SEDN_EXCHANGE, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                L.d(result.toString());
                hideLoading();
                not_data.setVisibility(View.GONE);
                MsgBean bean = JSON.parseObject(result,
                        MsgBean.class);
                if ("200".equals(bean.getResult())) {
                    //加载头部数据
                    EventBus.getDefault().post(
                            new RefershType(""));
                    Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
//                    getData();

                    getActivity().finish();
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
                } else if ("300".equals(bean.getResult())) {


                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(getActivity());

                    String phone = mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check", "0");
                    mCache.put("phone", "");

                    //激光注销掉
                    JPushInterface.setAlias(getActivity(), "0", new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });


                    showWarnDialog(phone);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                L.d("异常" + ex.toString());
                hideLoading();
                not_data.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();

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

    private void clickTab3Layout() {
        clickTab = 3;
        tv_one.setTextColor(Color.parseColor("#999999"));
        tv_two.setTextColor(Color.parseColor("#999999"));
        tv_three.setTextColor(Color.parseColor("#00CBAE"));
        rl_one.setBackgroundResource(R.drawable.default_frame);
        rl_two.setBackgroundResource(R.drawable.default_frame);
        rl_three.setBackgroundResource(R.drawable.green_frame);

        if(!TextUtils.isEmpty(pwd_text.getText().toString())&&!TextUtils.isEmpty(phone_text.getText().toString())){
            if (money <= data.getDoorsill().get(2).getDoorsill()) {
                login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                login_btn.setEnabled(false);
                tv_send.setText("余额不足");
            }else{
                login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                login_btn.setEnabled(true);
                tv_send.setText("提交申请");
            }
        }else{
            login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
            login_btn.setEnabled(false);
            tv_send.setText("提交申请");
        }
    }

    private void clickTab2Layout() {
        clickTab = 2;
        tv_one.setTextColor(Color.parseColor("#999999"));
        tv_two.setTextColor(Color.parseColor("#00CBAE"));
        tv_three.setTextColor(Color.parseColor("#999999"));
        rl_one.setBackgroundResource(R.drawable.default_frame);
        rl_two.setBackgroundResource(R.drawable.green_frame);
        rl_three.setBackgroundResource(R.drawable.default_frame);

        if(!TextUtils.isEmpty(pwd_text.getText().toString())&&!TextUtils.isEmpty(phone_text.getText().toString())){
            if (money <= data.getDoorsill().get(2).getDoorsill()) {
                login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                login_btn.setEnabled(false);
                tv_send.setText("余额不足");
            }else{
                login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                login_btn.setEnabled(true);
                tv_send.setText("提交申请");
            }
        }else{
            login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
            login_btn.setEnabled(false);
            tv_send.setText("提交申请");
        }
    }


    private void clickTab1Layout() {
        clickTab = 1;
        tv_one.setTextColor(Color.parseColor("#00CBAE"));
        tv_two.setTextColor(Color.parseColor("#999999"));
        tv_three.setTextColor(Color.parseColor("#999999"));
        rl_one.setBackgroundResource(R.drawable.green_frame);
        rl_two.setBackgroundResource(R.drawable.default_frame);
        rl_three.setBackgroundResource(R.drawable.default_frame);
        if(!TextUtils.isEmpty(pwd_text.getText().toString())&&!TextUtils.isEmpty(phone_text.getText().toString())){
            if (money <= data.getDoorsill().get(2).getDoorsill()) {
                login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                login_btn.setEnabled(false);
                tv_send.setText("余额不足");
            }else{
                login_btn.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                login_btn.setEnabled(true);
                tv_send.setText("提交申请");
            }
        }else{
            login_btn.setBackgroundColor(getResources().getColor(R.color.gry_btn));
            login_btn.setEnabled(false);
            tv_send.setText("提交申请");
        }

    }

    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Qbfragment");
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Qbfragment");
    }
}
