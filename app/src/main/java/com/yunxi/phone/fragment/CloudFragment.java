package com.yunxi.phone.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.activity.AboutActivity;
import com.yunxi.phone.activity.CloudDetailsActivity;
import com.yunxi.phone.activity.DownloadTaskActivity;
import com.yunxi.phone.activity.ExchangeActivity;
import com.yunxi.phone.activity.IncomeRankingActivity;
import com.yunxi.phone.activity.InvateActivity;
import com.yunxi.phone.activity.LoginActivity;
import com.yunxi.phone.activity.NewsSingleActivity;
import com.yunxi.phone.activity.OnlineGameActivity;
import com.yunxi.phone.activity.SendActivity;
import com.yunxi.phone.activity.SettingActivity;
import com.yunxi.phone.activity.SportActivity;
import com.yunxi.phone.activity.WebActivity;
import com.yunxi.phone.adapter.CloudCenterAdapter;
import com.yunxi.phone.bean.CloudBean;
import com.yunxi.phone.bean.CloudContentForBannerBean;
import com.yunxi.phone.bean.CloudContentForJifenBean;
import com.yunxi.phone.bean.NewsBean;
import com.yunxi.phone.bean.NewsContentBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.RefershType;
import com.yunxi.phone.service.StepService;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.CarouselViewForBanner;
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

public class CloudFragment extends Fragment implements OnClickListener {


    private LayoutInflater mInflater;

    private boolean mIsLogin;

    private SharedPreferences mLoginPreference;

    private CarouselViewForBanner banner;
    private AutoLinearLayout banner_layout;

    private AutoRelativeLayout setting;

    private AutoRelativeLayout hot_game_layout;
    private AutoRelativeLayout invate_layout;
    private AutoRelativeLayout income_layout;
    private AutoRelativeLayout experience_layout;
    private AutoRelativeLayout send_layout;//转发奖励
    private AutoRelativeLayout sport_layout;
    private AutoRelativeLayout user_info;

    private CloudCenterAdapter mAdapter;

    private int page=1;

    private boolean isRefresh;

    private AutoLoadListView cloud_listview;

    private ArrayList<CloudContentForBannerBean> bannerList;

    private MySwipeRefreshLayout swipeRefreshLayout;

    private TextView cloud_total,cloud_use,cloud_out;


    private ArrayList<NewsContentBean> newsList;

    private AutoRelativeLayout top_btn,news_kinds;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    private AutoRelativeLayout data_zero;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.cloud_fragment, container, false);

        mInflater = LayoutInflater.from(getActivity());

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }


    private void init(View view) {

        mSharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        EventBus.getDefault().register(this);

        setting = (AutoRelativeLayout) view.findViewById(R.id.setting_icon);
        setting.setOnClickListener(this);


        top_btn=(AutoRelativeLayout)view.findViewById(R.id.top_btn);
        top_btn.setOnClickListener(this);

        news_kinds=(AutoRelativeLayout)view.findViewById(R.id.news_kinds);
        news_kinds.setOnClickListener(this);

        cloud_listview=(AutoLoadListView)view.findViewById(R.id.cloud_listview);

        swipeRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        loading = (AutoLinearLayout) view.findViewById(R.id.loading);
        not_data = (AutoRelativeLayout) view.findViewById(R.id.not_data);
        data_zero = (AutoRelativeLayout) view.findViewById(R.id.data_zero);
        get_data = (AutoRelativeLayout) view.findViewById(R.id.get_data);
        get_data.setOnClickListener(this);
        View header=getActivity().getLayoutInflater().inflate(R.layout.cloud_center_header,null);

        initHeader(header);

        cloud_listview.addHeaderView(header);

        mAdapter=new CloudCenterAdapter(getActivity());
        cloud_listview.setAdapter(mAdapter);
        cloud_listview.setHasMoreItems(false);

//        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                isRefresh = true;
                page = 1;

                loadBannerData();

                loadNewsData(1);

            }
        });



        //加载更多

        cloud_listview.setPagingableListener(new AutoLoadListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {

                loadNewsData(page);

            }
        });


        cloud_listview.setBackTopListener(new AutoLoadListView.BackTop() {


            @Override
            public void onBackTop(int state) {

                switch (state) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时

                        L.d("滚动的距离是多少：" + getScrollY());
                        if (getScrollY() > 10000) {
                            top_btn.setVisibility(View.VISIBLE);
                        }
                        // 判断滚动到顶部
                        if (cloud_listview.getFirstVisiblePosition() == 0) {
                            top_btn.setVisibility(View.GONE);
                        }

                        break;
                }

            }
        });

            showLoading();
            loadBannerData();
            loadNewsData(page);
    }



    public int getScrollY() {
        View c = cloud_listview.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = cloud_listview.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight() ;
    }



    boolean isRead = false;


    private boolean checkLoginStatue(){

        mLoginPreference = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        mIsLogin = mLoginPreference.getBoolean(KEY_IS_LOGIN, false);

        return mIsLogin;
    }





    private void initHeader(View view){
        banner = (CarouselViewForBanner) view.findViewById(R.id.carouseView);
        banner_layout = (AutoLinearLayout) view.findViewById(R.id.banner_layout);

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        AutoLinearLayout.LayoutParams linearParams = (AutoLinearLayout.LayoutParams) banner_layout.getLayoutParams(); //
        linearParams.height = (int) ((float) width * 0.33);//
        banner_layout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件myGrid

        hot_game_layout=(AutoRelativeLayout)view.findViewById(R.id.hot_game_layout);
        hot_game_layout.setOnClickListener(this);
        income_layout=(AutoRelativeLayout)view.findViewById(R.id.rank_layout);
        income_layout.setOnClickListener(this);


        invate_layout=(AutoRelativeLayout)view.findViewById(R.id.invate_layout);
        invate_layout.setOnClickListener(this);


        experience_layout=(AutoRelativeLayout)view.findViewById(R.id.experience_layout);
        experience_layout.setOnClickListener(this);

        send_layout=(AutoRelativeLayout)view.findViewById(R.id.send_layout);
        send_layout.setOnClickListener(this);

        sport_layout=(AutoRelativeLayout)view.findViewById(R.id.sport_layout);
        sport_layout.setOnClickListener(this);


        cloud_total=(TextView)view.findViewById(R.id.cloud_total);
        cloud_out=(TextView)view.findViewById(R.id.cloud_out);
        cloud_use=(TextView)view.findViewById(R.id.cloud_use);

        user_info=(AutoRelativeLayout) view.findViewById(R.id.user_info);
        user_info.setOnClickListener(this);

    }



    private void loadBannerData(){

        Map<String, Object> map = new HashMap<>();

        final ACache mCache = ACache.get(getActivity());

        L.d("传入云朵中心的uid===="+mCache.getAsString("user_id"));

        if("".equals(mCache.getAsString("user_id")) || mCache.getAsString("user_id")==null){
            map.put("user_id","0");
        }else{
            map.put("user_id",mCache.getAsString("user_id"));
        }

        if("".equals(mCache.getAsString("token")) || mCache.getAsString("token")==null){
            map.put("userkey","0");
        }else{
            map.put("userkey",mCache.getAsString("token"));
        }


        XUtil.Post(getActivity(),AddressApi.CLOUD_CENTER, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                hideLoading();
                L.d("获得云朵首页的结果======="+result.toString());

                CloudBean bean = JSON.parseObject(result,
                        CloudBean.class);
                not_data.setVisibility(View.GONE);
                if ("200".equals(bean.getResult()) ){

                    //加载头部数据

                    bannerList=bean.getData().getImgresult();

                    loadBanner(bannerList,bean.getData().getUser());

                    if("1".equals(bean.getData().getNotice())){
                        //有未读
                        isRead=true;
                    }
                    mCache.put("noRead",isRead);

                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
                }else if("300".equals(bean.getResult())){



                    bannerList=bean.getData().getImgresult();

                    loadBanner(bannerList,bean.getData().getUser());

                    if("1".equals(bean.getData().getNotice())){
                        //有未读
                        isRead=true;
                    }
                    mCache.put("noRead",isRead);


                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(getActivity());

                    String phone =mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check","0");
                    mCache.put("phone","");

                    //激光注销掉
                    JPushInterface.setAlias(getActivity(), "0", new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });


//                    showWarnDialog(phone);



                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
                L.d("异常" + ex.toString());
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
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }


    private void loadNewsData(int pager){

        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(getActivity());

        if("".equals(mCache.getAsString("user_id")) || mCache.getAsString("user_id")==null){
            map.put("user_id","0");
        }else{
            map.put("user_id",mCache.getAsString("user_id"));
        }

        if("".equals(mCache.getAsString("token")) || mCache.getAsString("token")==null){
            map.put("userkey","0");
        }else{
            map.put("userkey",mCache.getAsString("token"));
        }

        map.put("page", pager);


        XUtil.Post(getActivity(),AddressApi.NEWS_LIST, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                L.d("=======新请求的结果======="+result.toString());

              boolean hasMoreData = true;

                NewsBean bean = JSON.parseObject(result,
                        NewsBean.class);



                if ("200".equals(bean.getResult()) ){

                    newsList=bean.getData();

                    if (newsList.size() > 0) {

                        if (isRefresh) {
                            mAdapter.clear();
                            mAdapter.notifyDataSetChanged();
                            isRefresh = false;
                        }


                        mAdapter.addAll(newsList);
                        mAdapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);
                        cloud_listview.setIsLoading(false);

                        cloud_listview.setHasMoreItems(hasMoreData);

                        page = page + 1;



                    } else {
                        hasMoreData = false;
                        swipeRefreshLayout.setRefreshing(false);
                        cloud_listview.setHasMoreItems(hasMoreData);
                        cloud_listview.setIsLoading(false);

                    }


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();

                    swipeRefreshLayout.setRefreshing(false);
                    cloud_listview.setHasMoreItems(false);
                    cloud_listview.setIsLoading(false);
                }else if("300".equals(bean.getResult())){

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(getActivity());

                    String phone =mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check","0");
                    mCache.put("phone","");

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

                L.d("异常"+ex.toString());

                swipeRefreshLayout.setRefreshing(false);
                cloud_listview.setHasMoreItems(false);
                cloud_listview.setIsLoading(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }







    private void loadBanner(final ArrayList<CloudContentForBannerBean> list, CloudContentForJifenBean user) {
        banner.setAdapter(new CarouselViewForBanner.Adapter() {
            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public View getView(final int position) {
                View view = mInflater.inflate(R.layout.banner_item, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.image);

                Glide.with(getActivity()).load(list.get(position).getImgurl()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop().placeholder(R.mipmap.placeholder_banner).into(imageView);

                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ("1".equals(list.get(position).getType())) {
                            //
                            if ("1".equals(list.get(position).getSub_type())) {
                                Intent i = new Intent(getActivity(), AboutActivity.class);
                                startActivity(i);
                            } else if ("2".equals(list.get(position).getSub_type())) {

                                Intent i = new Intent(getActivity(), IncomeRankingActivity.class);
                                startActivity(i);

                            }

                        } else if ("2".equals(list.get(position).getType())) {

                            Intent i = new Intent(getActivity(), WebActivity.class);
                            i.putExtra("url", list.get(position).getUrl());
                            startActivity(i);

                        }


                    }
                });

                return view;
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });

        if("".equals(user.getIntegral())){
            cloud_use.setText("--");
        }else{
            cloud_use.setText(user.getIntegral());
        }

        if("".equals(user.getIntegral_all())){
            cloud_total.setText("--");
        }else{
            cloud_total.setText(user.getIntegral_all());
        }

        if("".equals(user.getIntegral_consume())){
            cloud_out.setText("--");
        }else{
            cloud_out.setText(user.getIntegral_consume());
        }

    }


    public void onEventMainThread(RefershType event) {
        //刷新数据
        loadBannerData();

        loadNewsData(1);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    /**
     * 滚动ListView到指定位置
     *
     * @param pos
     */
    private void setListViewPos(int pos) {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
//            cloud_listview.smoothScrollToPosition(pos);
            cloud_listview.setSelection(pos);
            top_btn.setVisibility(View.GONE);
        } else {
            cloud_listview.setSelection(pos);
            top_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.top_btn:

                setListViewPos(0);

                break;


            case R.id.news_kinds:

                Intent news_kinds=new Intent(getActivity(), NewsSingleActivity.class);
                startActivity(news_kinds);

                break;

            case R.id.setting_icon:

                if(checkLoginStatue()){

                    //去设置页面

                    Intent i = new Intent(getActivity(), SettingActivity.class);
                    startActivity(i);


                }else{
                    //去登录
                    Intent  i= new Intent(getActivity(), LoginActivity.class);
                    i.putExtra("from","app_in");
                    startActivity(i);
                }


                break;

            case R.id.hot_game_layout:
                Intent game = new Intent(getActivity(), OnlineGameActivity.class);
                startActivity(game);

                break;

            case R.id.invate_layout:
                if(checkLoginStatue()){
                    //去邀请页面
                    Intent intent = new Intent(getActivity(),InvateActivity.class);
                    startActivity(intent);
                }else{
                    //去登录
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from","app_in");
                    startActivity(login);
                }




                break;

            case R.id.experience_layout:

                if(checkLoginStatue()){

                    //去下载页面

                    Intent ex = new Intent(getActivity(), DownloadTaskActivity.class);
                    startActivity(ex);

                }else{
                    //去登录
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from","app_in");
                    startActivity(login);
                }




                break;
            case R.id.rank_layout:

                if(checkLoginStatue()){


                    Intent rank = new Intent(getActivity(), ExchangeActivity.class);
                    startActivity(rank);

                }else{
                    //去登录
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from","app_in");
                    startActivity(login);
                }


                break;

            case R.id.send_layout:

                if(checkLoginStatue()){

                    //去转发页面

                    Intent s=new Intent(getActivity(), SendActivity.class);
                    startActivity(s);


                }else{
                    //去登录
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from","app_in");
                    startActivity(login);
                }

                break;

            case R.id.sport_layout:
                if(checkLoginStatue()){
                    Intent intent = new Intent(getActivity(), StepService.class);
                    getActivity().startService(intent);
                    //去运动页面
                    Intent sport=new Intent(getActivity(), SportActivity.class);
                    startActivity(sport);


                }else{
                    //去登录
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from","app_in");
                    startActivity(login);
                }

                break;

            case R.id.user_info:
                if(checkLoginStatue()){

                    //去更多页面
                    Intent c = new Intent(getActivity(), CloudDetailsActivity.class);
                    startActivity(c);


                }else{
                    //去登录
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from","app_in");
                    startActivity(login);
                }

                break;

            case R.id.get_data:
                showLoading();
                loadBannerData();

                loadNewsData(1);
                break;

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
        MobclickAgent.onPageStart("CloudFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CloudFragment");
    }
}
