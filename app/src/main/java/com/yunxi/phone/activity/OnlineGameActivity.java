package com.yunxi.phone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.OnlineGameAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.OnlineGameBean;
import com.yunxi.phone.bean.OnlineGameContentBean;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MySwipeRefreshLayout;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bond on 16/2/19.
 */
public class OnlineGameActivity extends BaseActivity implements View.OnClickListener{

    private AutoRelativeLayout back;

    private AutoLoadListView game_listview;

    private OnlineGameAdapter mAdapter;

    private MySwipeRefreshLayout mSwipeRefreshLayout;

    private int page=1;

    private boolean isRefresh;

    private ArrayList<OnlineGameContentBean> beans;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    private AutoRelativeLayout data_zero;
    private AutoLinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_online_game;
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

        mAdapter=new OnlineGameAdapter(OnlineGameActivity.this);
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

    private void loadData(final int pager){
        L.d("执行了");

        Map<String, Object> map = new HashMap<>();

        map.put("page", pager);


        XUtil.Post3(AddressApi.GAME_LIST, map, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                L.d("gamelist=" + result);
                not_data.setVisibility(View.GONE);
                boolean hasMoreData = true;
                hideLoading();
                OnlineGameBean bean = JSON.parseObject(result,
                        OnlineGameBean.class);

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
                        if(pager==1){
                            data_zero.setVisibility(View.VISIBLE);
                        }
                        hasMoreData = false;

                        mSwipeRefreshLayout.setRefreshing(false);
                        game_listview.setHasMoreItems(hasMoreData);
                        game_listview.setIsLoading(false);

                    }


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(OnlineGameActivity.this, "失败了", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    game_listview.setIsLoading(false);
                    game_listview.setHasMoreItems(false);
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
