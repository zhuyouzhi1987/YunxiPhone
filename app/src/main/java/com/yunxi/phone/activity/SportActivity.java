package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.SportAdapter;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.IntegralBean;
import com.yunxi.phone.bean.MsgBean;
import com.yunxi.phone.bean.RunningBaseBean;
import com.yunxi.phone.bean.RunningBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.RefershType;
import com.yunxi.phone.eventtype.StepType;
import com.yunxi.phone.service.StepData;
import com.yunxi.phone.service.StepDcretor;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.MySwipeRefreshLayout;
import com.yunxi.phone.utils.NetUtil;
import com.yunxi.phone.utils.NumberRunView;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by bond on 16/2/19.
 */
public class SportActivity extends BaseActivity implements View.OnClickListener {

    private AutoRelativeLayout back;

    private AutoLoadListView sport_listview;

    private SportAdapter mAdapter;

    private MySwipeRefreshLayout mSwipeRefreshLayout;

    private boolean isRefresh;

    private ArrayList<IntegralBean> beans;

    RunningBean bean;
    private NumberRunView today_size;

    private AutoRelativeLayout sport_details_btn;

    private TextView spprt_tips;

    private TextView get_goods_btn;
    private AutoRelativeLayout step_bg;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout not_wifi;
    private AutoRelativeLayout get_wifi;
    private AutoRelativeLayout get_data;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        sport_details_btn = $(R.id.sport_details_btn);
        sport_listview = $(R.id.sport_listview);
        mSwipeRefreshLayout = $(R.id.swipe_refresh);
        //设置圈圈颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        loading=$(R.id.loading);
        not_data=$(R.id.not_data);
        not_wifi=$(R.id.not_wifi);
        get_wifi=$(R.id.get_wifi);
        get_data=$(R.id.get_data);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        sport_details_btn.setOnClickListener(this);
        get_wifi.setOnClickListener(this);
        get_data.setOnClickListener(this);
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        EventBus.getDefault().register(this);

        View header = getLayoutInflater().inflate(R.layout.sport_header, null);

        initHeader(header);

        sport_listview.addHeaderView(header);

        mAdapter = new SportAdapter(SportActivity.this);
        sport_listview.setAdapter(mAdapter);
        sport_listview.setHasMoreItems(false);
        sport_listview.setIsLoading(false);


        sport_listview.setBackTopListener(new AutoLoadListView.BackTop() {
            @Override
            public void onBackTop(int state) {

            }
        });


        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                isRefresh = true;
                loadData();

            }
        });
    }

    public void onEventMainThread(StepType event) {
        L.d(event.getMsg() + "..");
        String todayDate = getTodayDate();
//
//        if(null==bean){
//            step_bg.setVisibility(View.GONE);
//            return;
//        }
        if(null==bean){
            return;
        }
        if(!NetUtil.hasNetwork(getApplicationContext())){
            return;
        }
        if(!todayDate.equals(bean.getData().getDatetime())){
            //日期有出入
            spprt_tips.setText("手机时间不正确 , 无法记录奖励");
            get_goods_btn.setText("设置时间");
            get_goods_btn.setEnabled(true);
            get_goods_btn.setBackgroundResource(R.drawable.blue_yuanjiao_itemclick_selector);
            isTrueTime = false;
            today_size.setShowNum(0+ "", 0);
            return;
        }else{
            isTrueTime=true;
        }
        today_size.setText(event.getMsg());

        int step = Integer.parseInt(event.getMsg());
        //------

        int money = 0;
        int xianbushu = 0;
        boolean shifoulingqu = true;//是否领取全部
        boolean all = true;
        for (RunningBaseBean runningBaseBean : bean.getData().getRunningbase()) {
            int status =runningBaseBean.IsReceive;
            int steps = runningBaseBean.getRun_Steps();
            int integral = runningBaseBean.getRun_Integral();
            if (step >= runningBaseBean.getRun_Steps()) {//
                if (status == 0)
                {
                    money = money + integral;
                    shifoulingqu = false;
//                    xianbushu = steps;
                    xianbushu = step;
                    all = false;
                }
            } else {
                if (shifoulingqu)
                {
                    money = money + integral;
                    xianbushu = steps;
//                    xianbushu = step;
                    all = false;
                }
                break;
            }
        }
        if (all)
        {
            spprt_tips.setText("今日已完成全部运动任务 !");
            get_goods_btn.setText("明日再来");
            get_goods_btn.setEnabled(false);
        }else{
            if (shifoulingqu)
            {
                spprt_tips.setText("今日完成运动" + xianbushu + "步 , " + "可获得" + money + "云朵 !");
                get_goods_btn.setEnabled(false);
                get_goods_btn.setBackgroundResource(R.drawable.gry_yuanjiao_itemclick_selector);
                get_goods_btn.setText("尚未达标");
            }
            else
            {
                spprt_tips.setText( "今日已完成运动" + xianbushu + "步，" + "可获得" + money + "云朵 !");
                get_goods_btn.setEnabled(true);
                get_goods_btn.setBackgroundResource(R.drawable.red_yuanjiao_itemclick_selector);
                get_goods_btn.setText("领取奖励");
            }
        }
    }

    private void initHeader(View view) {

        today_size = (NumberRunView) view.findViewById(R.id.step_size);

        today_size.setShowNum(0+ "", 0);

        spprt_tips = (TextView) view.findViewById(R.id.spprt_tips);

        get_goods_btn = (TextView) view.findViewById(R.id.get_goods_btn);
        step_bg = (AutoRelativeLayout) view.findViewById(R.id.step_bg);
        get_goods_btn.setOnClickListener(this);

    }


    private void loadData() {

        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(this);

        L.d( "传入云朵中心的uid====" + mCache.getAsString("user_id"));

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


        XUtil.Post(SportActivity.this, AddressApi.RUNNING_INFO, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                hideLoading();
                L.d(result);
                boolean hasMoreData = true;
                bean = JSON.parseObject(result,
                        RunningBean.class);
                not_wifi.setVisibility(View.GONE);
                not_data.setVisibility(View.GONE);
                if ("200".equals(bean.getResult())) {
//                    step_bg.setVisibility(View.VISIBLE);
                    beans = bean.getData().getIntegral();
                    if (beans.size() > 0) {
                        if (isRefresh) {
                            mAdapter.clear();
                            mAdapter.notifyDataSetChanged();
                            isRefresh = false;
                        }
                        mAdapter.addAll(beans);
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        sport_listview.setIsLoading(false);
                        sport_listview.setHasMoreItems(false);
                    } else {
                        hasMoreData = false;
                        mSwipeRefreshLayout.setRefreshing(false);
                        sport_listview.setHasMoreItems(hasMoreData);
                        sport_listview.setIsLoading(false);
                    }
                    initView(bean.getData().getDatetime());
                } else if ("500".equals(bean.getResult())) {

                    Toast.makeText(SportActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    sport_listview.setIsLoading(false);
                    sport_listview.setHasMoreItems(false);
                } else if ("300".equals(bean.getResult())) {

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(SportActivity.this);

                    String phone = mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check", "0");
                    mCache.put("phone", "");

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
                noNetView();
                Toast.makeText(SportActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                sport_listview.setIsLoading(false);
                sport_listview.setHasMoreItems(false);
//                not_data.setVisibility(View.VISIBLE);
//                step_bg.setVisibility(View.GONE);
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
        MyAlertDialog dialog = new MyAlertDialog(SportActivity.this)
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

                Intent login = new Intent(SportActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }

    private void noNetView() {
        spprt_tips.setText("当前网络不佳 , 无法开启运动奖励 ! ");
        get_goods_btn.setText("检查网络");
        get_goods_btn.setEnabled(true);
        get_goods_btn.setBackgroundResource(R.drawable.blue_yuanjiao_itemclick_selector);
        isTrueTime = false;
    }

    boolean isTrueTime=false;
    int setp=0;
    private void initView(String datetime) {
        String todayDate = getTodayDate();
        if(!todayDate.equals(datetime)){
            //日期有出入
            spprt_tips.setText("手机时间不正确 , 无法记录奖励");
            get_goods_btn.setText("设置时间");
            get_goods_btn.setEnabled(true);
            get_goods_btn.setBackgroundResource(R.drawable.blue_yuanjiao_itemclick_selector);
            isTrueTime = false;
            today_size.setShowNum(0+ "", 0);
            return;
        }else{
            isTrueTime=true;
        }

        try {
            StepData stepData = YunxiApplication.db.selector(StepData.class).where("today", "=", getTodayDate()).findFirst();
            if(stepData==null){
                setp = 0;
            }else{
                setp=Integer.parseInt(stepData.getStep());
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        today_size.setShowNum(setp+ "", 0);
        today_size.startRun();


        int money = 0;
        int xianbushu = 0;
        boolean shifoulingqu = true;//是否领取全部
        boolean all = true;
        for (RunningBaseBean runningBaseBean : bean.getData().getRunningbase()) {
            int status =runningBaseBean.IsReceive;
            int steps = runningBaseBean.getRun_Steps();
            int integral = runningBaseBean.getRun_Integral();
            if (StepDcretor.CURRENT_SETP >= runningBaseBean.getRun_Steps()) {//
                if (status == 0)
                {
                    money = money + integral;
                    shifoulingqu = false;
                    xianbushu = setp;
                    all = false;
                }
            } else {
                if (shifoulingqu)
                {
                    money = money + integral;
                    xianbushu = steps;
                    all = false;
                    spprt_tips.setText("今日完成运动" + xianbushu + "步 , " + "可获得" + money + "云朵 !");
                    get_goods_btn.setText("领取奖励");
                    get_goods_btn.setEnabled(false);
                    get_goods_btn.setBackgroundResource(R.drawable.gry_yuanjiao_itemclick_selector);
                }
                break;
            }

        }
        if (all)
        {
            spprt_tips.setText("今日已完成全部运动任务 !");
            get_goods_btn.setText("明日再来 !");
            get_goods_btn.setEnabled(false);
            get_goods_btn.setBackgroundResource(R.drawable.gry_yuanjiao_itemclick_selector);
        }else{
            if (shifoulingqu)
            {
                spprt_tips.setText("今日完成运动" + xianbushu + "步 , " + "可获得" + money + "云朵 !");
                get_goods_btn.setEnabled(false);
                get_goods_btn.setBackgroundResource(R.drawable.gry_yuanjiao_itemclick_selector);
                get_goods_btn.setText("尚未达标");
            }
            else
            {
                spprt_tips.setText("今日已完成运动" + xianbushu + "步，" + "可获得" + money + "云朵 !");
                get_goods_btn.setEnabled(true);
                get_goods_btn.setBackgroundResource(R.drawable.red_yuanjiao_itemclick_selector);
                get_goods_btn.setText("领取奖励");
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(NetUtil.hasNetwork(getApplicationContext())) {
            showLoading();
            loadData();
//        }else{
//            not_wifi.setVisibility(View.VISIBLE);
//        }
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

            case R.id.sport_details_btn:
                if(null!=bean){
                    Intent intent = new Intent(getApplicationContext(), RewardRulesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", bean.getData());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.get_goods_btn:
                //上报步数
                if(!isTrueTime){
                    Intent i =  new Intent(Settings.ACTION_SETTINGS );
                    startActivity(i);
                }else{
                        sendStep();
                }
                break;
            case R.id.get_wifi:
                Intent i =  new Intent(Settings.ACTION_SETTINGS);
                startActivity(i);
                break;
            case R.id.get_data:
                showLoading();
                loadData();
                break;
        }
    }

    private void sendStep() {
        showLoading();
        get_goods_btn.setEnabled(false);
        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(this);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        map.put("steps", StepDcretor.CURRENT_SETP);
        XUtil.Post(SportActivity.this, AddressApi.REPORT_STEP, map, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                L.d(result);
                hideLoading();
                MsgBean bean = JSON.parseObject(result,
                        MsgBean.class);
                if ("200".equals(bean.getResult())) {
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(SportActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                } else if("300".equals(bean.getResult())){


                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(SportActivity.this);

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




                isRefresh = true;
                get_goods_btn.setEnabled(true);
                loadData();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
                Toast.makeText(SportActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                get_goods_btn.setEnabled(true);
//                not_data.setVisibility(View.VISIBLE);
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

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

//    public void onEventMainThread(RefershType event) {
//        //刷新数据
//        showLoading();
//        loadData();
//    }
}
