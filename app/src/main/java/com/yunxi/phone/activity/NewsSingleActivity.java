package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.bean.NewsKindBean;
import com.yunxi.phone.bean.NewsKindContentBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.fragment.NewsItemFragment;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.StatusBarUtil;
import com.yunxi.phone.utils.XUtil;
import com.yunxi.phone.viewpagerindicator.TabPageIndicator;
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
 * Created by bond on 2017/1/23.
 */

public class NewsSingleActivity extends FragmentActivity implements View.OnClickListener {


    private AutoRelativeLayout back;

    private FragmentPagerAdapter adapter;

    private  ViewPager pager;

    private TabPageIndicator indicator;

    private ArrayList<NewsKindContentBean> lists;

    private AutoRelativeLayout shoucang;

    private boolean mIsLogin;

    private SharedPreferences mLoginPreference;


    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_main);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.appMainColor));


        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);


        back = (AutoRelativeLayout) findViewById(R.id.back);
        back.setOnClickListener(this);

        shoucang=(AutoRelativeLayout)findViewById(R.id.shoucang);
        shoucang.setOnClickListener(this);


        if(checkLoginStatue()){

            shoucang.setVisibility(View.VISIBLE);

        }else {
            shoucang.setVisibility(View.GONE);
        }


        pager = (ViewPager) findViewById(R.id.pager);



        //实例化TabPageIndicator然后设置ViewPager与之关联
        indicator = (TabPageIndicator) findViewById(R.id.indicator);


        //如果我们要对ViewPager设置监听，用indicator设置就行了
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });


        loadKindData();


    }


    private void loadKindData() {

        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(NewsSingleActivity.this);

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

        XUtil.Post(NewsSingleActivity.this, AddressApi.GET_NEWS_KIND, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                L.d("=======新闻分类的结果=======" + result.toString());

                NewsKindBean bean = JSON.parseObject(result,
                        NewsKindBean.class);


                if ("200".equals(bean.getResult())) {


                    lists=bean.getData();

                    L.d("请求失败了");

                    adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
                    pager.setAdapter(adapter);
                    pager.setOffscreenPageLimit(lists.size());
                    indicator.setViewPager(pager);


                    indicator.setVisibility(View.VISIBLE);



                } else if ("500".equals(bean.getResult())) {



                    Toast.makeText(NewsSingleActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                }else if("300".equals(bean.getResult())){

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(NewsSingleActivity.this);

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

                L.d("异常" + ex.toString());

                Toast.makeText(NewsSingleActivity.this, "网络错误", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    private void showWarnDialog(String phone){
        MyAlertDialog dialog = new MyAlertDialog(NewsSingleActivity.this)
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

                Intent login = new Intent(NewsSingleActivity.this, LoginActivity.class);
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

            case R.id.shoucang:

                Intent collection = new Intent(NewsSingleActivity.this,MyCollectionActivity.class);
                startActivity(collection);

                break;
        }
    }


    private boolean checkLoginStatue(){

        mLoginPreference = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        mIsLogin = mLoginPreference.getBoolean(KEY_IS_LOGIN, false);

        return mIsLogin;
    }



    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //新建一个Fragment来展示ViewPager item的内容，并传递参数
            Fragment fragment = new NewsItemFragment();
            Bundle args = new Bundle();
            args.putInt("arg",lists.get(position).getCategoryID());
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return lists.get(position % lists.size()).getCategoryName();
        }

        @Override
        public int getCount() {
            return lists.size();
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
}
