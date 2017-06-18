package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.CloudDetailsAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.JifenBean;
import com.yunxi.phone.bean.JifenDetailsBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.MySwipeRefreshLayout;
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
 * Created by bond on 16/2/19.
 */
public class CloudDetailsActivity extends BaseActivity implements View.OnClickListener {

    private AutoRelativeLayout back;

    private AutoLoadListView game_listview;

    private CloudDetailsAdapter mAdapter;

    private MySwipeRefreshLayout mSwipeRefreshLayout;

    private int page = 1;

    private boolean isRefresh;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private ArrayList<JifenDetailsBean> beans;
    private AutoRelativeLayout get_data;
    private AutoRelativeLayout data_zero;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cloud_details;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        game_listview = $(R.id.cloud_details_listview);
        mSwipeRefreshLayout = $(R.id.swipe_refresh);
        //设置圈圈颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        loading = $(R.id.loading);
        not_data = $(R.id.not_data);
        data_zero = $(R.id.data_zero);
        get_data = $(R.id.get_data);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        get_data.setOnClickListener(this);
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        mAdapter = new CloudDetailsAdapter(CloudDetailsActivity.this);
        game_listview.setAdapter(mAdapter);
        game_listview.setHasMoreItems(false);

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {


                isRefresh = true;
                page = 1;
                loadData(page);

            }
        });

        //加载更多

        game_listview.setPagingableListener(new AutoLoadListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {

                loadData(page);

            }
        });

        game_listview.setBackTopListener(new AutoLoadListView.BackTop() {
            @Override
            public void onBackTop(int state) {

            }
        });
            showLoading();
            loadData(page);

    }

    private void loadData(final int pager) {
        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(CloudDetailsActivity.this);

        map.put("page", pager);

        map.put("user_id", mCache.getAsString("user_id"));

        map.put("userkey", mCache.getAsString("token"));


        XUtil.Post(CloudDetailsActivity.this, AddressApi.CLOUD_DETAILS, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                boolean hasMoreData = true;
                hideLoading();
                not_data.setVisibility(View.GONE);
                JifenBean bean = JSON.parseObject(result,
                        JifenBean.class);

                if ("200".equals(bean.getResult())) {
                    beans = bean.getData().getList();


                    if (beans.size() > 0) {

                        if (isRefresh) {
                            mAdapter.clear();
                            mAdapter.notifyDataSetChanged();
                            isRefresh = false;
                        }


                        mAdapter.addAll(beans);
                        mAdapter.notifyDataSetChanged();

                        mSwipeRefreshLayout.setRefreshing(false);
                        game_listview.setIsLoading(false);

                        game_listview.setHasMoreItems(hasMoreData);


                        page = page + 1;


                    } else {
                        if(pager==1){
                            data_zero.setVisibility(View.VISIBLE);
                        }
                        hasMoreData = false;
                        mSwipeRefreshLayout.setRefreshing(false);
                        game_listview.setHasMoreItems(hasMoreData);
                        game_listview.setIsLoading(false);
                    }


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(CloudDetailsActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    game_listview.setIsLoading(false);
                    game_listview.setHasMoreItems(false);
                }else if("300".equals(bean.getResult())){


                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(CloudDetailsActivity.this);

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
                mSwipeRefreshLayout.setRefreshing(false);
                game_listview.setIsLoading(false);
                game_listview.setHasMoreItems(false);
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
        MyAlertDialog dialog = new MyAlertDialog(CloudDetailsActivity.this)
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

                Intent login = new Intent(CloudDetailsActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
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

                break;
            case R.id.get_data:
                showLoading();
                isRefresh=true;
                loadData(1);
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
