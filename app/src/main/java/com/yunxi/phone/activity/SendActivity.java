package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.SendAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.SendAdsBean;
import com.yunxi.phone.bean.SendAdsContentBean;
import com.yunxi.phone.bean.ShareAdsBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.ShareAdsType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.NetUtil;
import com.yunxi.phone.utils.UserInfoUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by bond on 16/2/19.
 */
public class SendActivity extends BaseActivity implements View.OnClickListener{

    private AutoRelativeLayout back,send_desc;

    private AutoLoadListView send_listview;

    private SendAdapter mAdapter;

    private ArrayList<SendAdsContentBean> beans;

    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
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
        return R.layout.activity_send;
    }

    @Override
    protected void findById() {
        back=$(R.id.back);
        send_desc=$(R.id.send_desc);
        send_listview=$(R.id.send_listview);
        loading=$(R.id.loading);
        not_data=$(R.id.not_data);
        get_data=$(R.id.get_data);
        data_zero = $(R.id.data_zero);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        send_desc.setOnClickListener(this);
        get_data.setOnClickListener(this);
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        EventBus.getDefault().register(this);

        mAdapter=new SendAdapter(SendActivity.this);
        send_listview.setAdapter(mAdapter);
        send_listview.setHasMoreItems(false);
        send_listview.setIsLoading(false);

        send_listview.setBackTopListener(new AutoLoadListView.BackTop() {
            @Override
            public void onBackTop(int state) {

            }
        });

            loadData();

    }

    private void loadData(){
        showLoading();

        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(SendActivity.this);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));


        XUtil.Post(SendActivity.this, AddressApi.SEND_ADS, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                hideLoading();
                L.d("gamelist=" + result);
                not_data.setVisibility(View.GONE);
                SendAdsBean bean = JSON.parseObject(result,
                        SendAdsBean.class);

                if ("200".equals(bean.getResult())) {
                    beans = bean.getData();


                    if (beans.size() > 0) {
                        mAdapter.clear();
                        mAdapter.addAll(beans);
                        mAdapter.notifyDataSetChanged();

                        send_listview.setIsLoading(false);
                        send_listview.setHasMoreItems(false);


                    } else {
                        data_zero.setVisibility(View.VISIBLE);
                        send_listview.setHasMoreItems(false);
                        send_listview.setIsLoading(false);

                    }


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(SendActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                    send_listview.setIsLoading(false);
                    send_listview.setHasMoreItems(false);
                }else if("300".equals(bean.getResult())){



                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(SendActivity.this);

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
                send_listview.setIsLoading(false);
                send_listview.setHasMoreItems(false);
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
        MyAlertDialog dialog = new MyAlertDialog(SendActivity.this)
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

                Intent login = new Intent(SendActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }


    private void prepareShareAds(final int ads_type,final String url,final String content){


        new Thread(new Runnable() {
            @Override
            public void run() {

                Intent send=new Intent();
                send.setAction(Intent.ACTION_SEND);
                if(ads_type==2){

                    L.d("转换成的url="+url);

                    Uri uri=Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), UserInfoUtil.getBitmap(url),null,null));
                    send.putExtra(Intent.EXTRA_STREAM,uri);

                }
                send.setType("image/*");
                send.putExtra("Kdescription",content);
                send.setClassName("com.tencent.mm","com.tencent.mm.ui.tools.ShareToTimeLineUI");
                startActivity(send);

            }
        }).start();

    }



    private void shareAds(int ads_id, String url, final int ads_type, final String content){

        showLoading();

        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(SendActivity.this);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        map.put("advertiseid", ads_id);
        map.put("url", url);


        XUtil.Post(SendActivity.this,AddressApi.SHARE_ADS, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                L.d("分享此广告="+result.toString());

                hideLoading();


                ShareAdsBean bean = JSON.parseObject(result,
                        ShareAdsBean.class);

                if ("200".equals(bean.getResult()) ){


                    prepareShareAds(ads_type,bean.getData(),content);


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(SendActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }else if("300".equals(bean.getResult())){


                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(SendActivity.this);

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
                Toast.makeText(SendActivity.this, "网络错误", Toast.LENGTH_SHORT).show();

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


    public void onEventMainThread(ShareAdsType event) {

        L.d("广告id="+event.getAdsId());
        L.d("广告url="+event.getUrl());
        L.d("广告类型="+event.getAdsType());
        L.d("广告内容="+event.getAdsContent());

        shareAds(event.getAdsId(), event.getUrl(), event.getAdsType(), event.getAdsContent());

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

            case R.id.send_desc:
                //转发说明
                Intent i=new Intent(SendActivity.this,SendDescActivity.class);
                startActivity(i);

                break;
            case R.id.get_data:
                loadData();
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
