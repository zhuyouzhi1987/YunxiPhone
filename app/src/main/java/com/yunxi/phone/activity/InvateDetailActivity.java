package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.InvateAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.InvateDetilBean;
import com.yunxi.phone.bean.InvateDetilDataBean;
import com.yunxi.phone.bean.JifenDetailsBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.MySwipeRefreshLayout;
import com.yunxi.phone.utils.NetUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/12.
 */
public class InvateDetailActivity extends BaseActivity implements View.OnClickListener {
    private AutoRelativeLayout back;
    private ListView game_listview;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    private MySwipeRefreshLayout mSwipeRefreshLayout;
    private int page = 1;
    private InvateAdapter mAdapter;
    private boolean isRefresh;
    private AutoRelativeLayout data_zero;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invate_detail;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        game_listview = $(R.id.cloud_details_listview);
        loading=$(R.id.loading);
        not_data=$(R.id.not_data);
        get_data=$(R.id.get_data);
        data_zero = $(R.id.data_zero);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        get_data.setOnClickListener(this);
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        mAdapter = new InvateAdapter(InvateDetailActivity.this);
        game_listview.setAdapter(mAdapter);
            loadData();
    }

    private ArrayList<JifenDetailsBean> beans;

    private void loadData() {
        showLoading();
        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(InvateDetailActivity.this);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        XUtil.Post(InvateDetailActivity.this, AddressApi.GET_INVITATION_INFO, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                L.d(result);
                hideLoading();
                not_data.setVisibility(View.GONE);
                InvateDetilDataBean bean = JSON.parseObject(result,
                        InvateDetilDataBean.class);
                if ("200".equals(bean.getResult())) {
                    ArrayList<InvateDetilBean> data = bean.getData();
                    if(data.size()>0){
                        mAdapter.clear();
                        mAdapter.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        data_zero.setVisibility(View.VISIBLE);
                    }

                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(InvateDetailActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                }else if("300".equals(bean.getResult())){


                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(InvateDetailActivity.this);

                    String phone =mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check","0");
                    mCache.put("phone","");

                    //激光注销掉
                    JPushInterface.setAlias(getApplicationContext(), "0", new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });


                    showWarnDialog(phone);

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


    private void showWarnDialog(String phone){
        MyAlertDialog dialog = new MyAlertDialog(InvateDetailActivity.this)
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

                Intent login = new Intent(InvateDetailActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


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
              loadData();
                break;
        }
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

    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}
