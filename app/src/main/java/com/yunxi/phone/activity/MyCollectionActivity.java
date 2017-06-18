package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.NewsKindAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.NewsBean;
import com.yunxi.phone.bean.NewsContentBean;
import com.yunxi.phone.eventtype.CollectType;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.L;
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
public class MyCollectionActivity extends BaseActivity implements View.OnClickListener{

    private AutoRelativeLayout back;

    private AutoLoadListView game_listview;

    private NewsKindAdapter mAdapter;

    private MySwipeRefreshLayout mSwipeRefreshLayout;

    private int page=1;

    private boolean isRefresh;

    private ArrayList<NewsContentBean> beans;
    private AutoLinearLayout loading;
    private AutoRelativeLayout get_data;
    private AutoRelativeLayout data_zero;
    private AutoRelativeLayout not_data;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected void findById() {
        back=$(R.id.back);
        game_listview=$(R.id.game_listview);
        mSwipeRefreshLayout=$(R.id.swipe_refresh);
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

        mAdapter=new NewsKindAdapter(MyCollectionActivity.this);
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

    private void loadData(int pager){

        Map<String, Object> map = new HashMap<>();


        final ACache mCache = ACache.get(MyCollectionActivity.this);
            map.put("user_id",mCache.getAsString("user_id"));
            map.put("userkey",mCache.getAsString("token"));
            map.put("page", pager);


        XUtil.Post(MyCollectionActivity.this,AddressApi.MY_COLLECTION, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                L.d("gamelist=" + result);
                hideLoading();
                not_data.setVisibility(View.GONE);
                boolean hasMoreData = true;

                NewsBean bean = JSON.parseObject(result,
                        NewsBean.class);

                if ("200".equals(bean.getResult())) {
                    beans = bean.getData();


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
                        hasMoreData = false;
                        if(page==1){
                            data_zero.setVisibility(View.VISIBLE);
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        game_listview.setHasMoreItems(hasMoreData);
                        game_listview.setIsLoading(false);

                    }


                } else if ("500".equals(bean.getResult())) {


                    mSwipeRefreshLayout.setRefreshing(false);
                    game_listview.setIsLoading(false);
                    game_listview.setHasMoreItems(false);
                }else if("300".equals(bean.getResult())){

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(MyCollectionActivity.this);

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

                L.d("收藏异常="+ex.toString());

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
        MyAlertDialog dialog = new MyAlertDialog(MyCollectionActivity.this)
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

                Intent login = new Intent(MyCollectionActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }



    public void onEventMainThread(CollectType event) {

        data_zero.setVisibility(View.GONE);

        isRefresh = true;
        page = 1;
        loadData(page);
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
