package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.mobads.appoffers.OffersManager;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.TiyanListAdapter;
import com.yunxi.phone.adapter.ZhuijiaListAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.MsgBean;
import com.yunxi.phone.bean.TaskAddContentBean;
import com.yunxi.phone.bean.TaskContentBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.fragment.TiyanFragment;
import com.yunxi.phone.fragment.ZhuiJiaFragment;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by bond on 16/2/19.
 */
public class DownloadTaskActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private AutoRelativeLayout back;
    private AutoRelativeLayout tips_btn;

    private ViewPager mPager;

    private AutoLoadListView tiyan_ListView;
    private AutoLoadListView zhuijia_ListView;

    private int tiyan_page = 1;
    private int zhuijia_page = 1;

    private boolean[] mIsLoadData;

    private static int TIYAN = 0;
    private static int ZHUIJIA = 1;

    private int mCurrentPage = TIYAN;//当前页是 体验

    private TiyanListAdapter tiyan_adapter;
    private ZhuijiaListAdapter zhuijia_adapter;

    private TextView tiyan_btn, zhuijia_btn;
    private FragmentStatePagerAdapter adapter;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;

    private View tiyanView, zhuijiaView;
    private TextView go_baidu;
    private List<Fragment> tabList = new ArrayList<Fragment>();
    private boolean tiyan_isLoadMore;
    private boolean zhuijia_isLoadMore;

    private ArrayList<TaskContentBean> task_beans;
    private ArrayList<TaskAddContentBean> task_add_beans;


    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    private AutoLinearLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_download_task;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        tips_btn = $(R.id.tips);
        go_baidu = $(R.id.go_baidu);
        mPager = $(R.id.pager);
        tiyan_btn = $(R.id.tiyan_btn);
        zhuijia_btn = $(R.id.zhuijia_btn);
        loading = $(R.id.loading);
        not_data = $(R.id.not_data);
        get_data = $(R.id.get_data);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        tips_btn.setOnClickListener(this);
        go_baidu.setOnClickListener(this);
        zhuijia_btn.setOnClickListener(this);
        tiyan_btn.setOnClickListener(this);

        mPager.setOnPageChangeListener(this);
    }


    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
showLoading();
        uploadAppPackage();

    }

    private void uploadAppPackage() {
        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(this);
        L.d("uid====" + mCache.getAsString("user_id"));

        map.put("user_id", mCache.getAsString("user_id"));

        map.put("userkey", mCache.getAsString("token"));
        StringBuffer sb = new StringBuffer();
        for (PackageInfo info : getPackageManager().getInstalledPackages(0)) {
            // 判断系统/非系统应用
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                sb.append(info.packageName + ",");//如果非系统应用，则添加至appList
                sb.append(info.applicationInfo.loadLabel(getPackageManager()).toString() + "|");
//                L.d("**************" + info.packageName + " | " + info.applicationInfo.loadLabel(getPackageManager()).toString());
            }
        }
        try {
            String mytext = java.net.URLEncoder.encode(sb.toString(), "utf-8");
            map.put("pakname", mytext);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        XUtil.Post(this, AddressApi.SEDN_APP, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                hideLoading();
                L.i("下载列表结果=====" + result.toString());
                not_data.setVisibility(View.GONE);
                MsgBean bean = JSON.parseObject(result,
                        MsgBean.class);
                if ("200".equals(bean.getResult())) {
                    initFragment();
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(DownloadTaskActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                } else if ("300".equals(bean.getResult())) {

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(DownloadTaskActivity.this);

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
                not_data.setVisibility(View.VISIBLE);
                Toast.makeText(DownloadTaskActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
        MyAlertDialog dialog = new MyAlertDialog(DownloadTaskActivity.this)
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

                Intent login = new Intent(DownloadTaskActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }


    private void initFragment() {
        TiyanFragment tiyanFragment = new TiyanFragment();
        ZhuiJiaFragment zhuijiaFragment = new ZhuiJiaFragment();
        tabList.add(tiyanFragment);
        tabList.add(zhuijiaFragment);
        adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public Fragment getItem(int position) {
                return tabList.get(position);
            }

            @Override
            public int getCount() {
                return tabList.size();
            }
        };

        mPager.setAdapter(adapter);
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(2);
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
        switch (view.getId()) {
            case R.id.back:

                finish();

                break;

            case R.id.tips:
                Intent i = new Intent(this,DownloadInfoActivity.class);
                startActivity(i);
                break;

            case R.id.tiyan_btn:

                mPager.setCurrentItem(0);

                break;

            case R.id.zhuijia_btn:

                mPager.setCurrentItem(1);

                break;
            case R.id.go_baidu:
                OffersManager.setUserName(this, "user name");
                Intent intent = new Intent(this, MsspWallActivity.class);
                startActivity(intent);
                break;
            case R.id.get_data:
                uploadAppPackage();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        switch (position) {
            case 0:
                tiyan_btn.setBackgroundResource(R.drawable.green_yuanjiao);
                tiyan_btn.setTextColor(getResources().getColor(R.color.white));
                zhuijia_btn.setBackgroundResource(R.drawable.white_yuanjiao);
                zhuijia_btn.setTextColor(getResources().getColor(R.color.appMainColor));
                mCurrentPage = position;


                break;

            case 1:

                zhuijia_btn.setBackgroundResource(R.drawable.green_yuanjiao);
                zhuijia_btn.setTextColor(getResources().getColor(R.color.white));
                tiyan_btn.setBackgroundResource(R.drawable.white_yuanjiao);
                tiyan_btn.setTextColor(getResources().getColor(R.color.appMainColor));
                mCurrentPage = position;

                break;
        }

    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

}
