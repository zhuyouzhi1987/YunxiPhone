package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.AppActivity;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.AddDeleteCollectBean;
import com.yunxi.phone.bean.CheckCollectBean;
import com.yunxi.phone.bean.CloudBean;
import com.yunxi.phone.eventtype.CollectType;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.YanZhengType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;


public class NewsDetailsActivity extends BaseActivity implements View.OnClickListener{
    private WebView web;
    private AutoRelativeLayout back;

    AdView adView;

    private AutoRelativeLayout rootLayout;

    private String url;

    private int id;

    private AutoRelativeLayout collect_layout;
    private ImageView collect_icon;


    private boolean mIsLogin;

    private SharedPreferences mLoginPreference;


    private String collect_statue="";


    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    @Override
    protected void findById() {

//        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        web = $(R.id.web);
        back=$(R.id.back);
        rootLayout=$(R.id.root_layout);
        collect_layout=$(R.id.collect_layout);
        collect_icon=$(R.id.collect_icon);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.news_details;
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        collect_layout.setOnClickListener(this);

    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        url= getIntent().getStringExtra("url");
        id= getIntent().getIntExtra("ads_id",0);


        if(checkLoginStatue()){

            collect_layout.setVisibility(View.VISIBLE);
            loadCollectData(id);

        }else {
            collect_layout.setVisibility(View.GONE);
        }


        initWebview();

    }

    private void initWebview() {


        // 默认请求http广告，若需要请求https广告，请设置AdSettings.setSupportHttps为true
        // AdSettings.setSupportHttps(true);

        // 代码设置AppSid，此函数必须在AdView实例化前调用
        // AdView.setAppSid("debug");

        // 设置'广告着陆页'动作栏的颜色主题
        // 目前开放了七大主题：黑色、蓝色、咖啡色、绿色、藏青色、红色、白色(默认) 主题
//        AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_RED_THEME);

        //        另外，也可设置动作栏中单个元素的颜色, 颜色参数为四段制，0xFF(透明度, 一般填FF)DE(红)DA(绿)DB(蓝)
//        AppActivity.getActionBarColorTheme().set[Background|Title|Progress|Close]Color(0xFFDEDADB);
        AppActivity.getActionBarColorTheme().setBackgroundColor(Color.parseColor("#00CBAE"));
        AppActivity.getActionBarColorTheme().setTitleColor(Color.parseColor("#FFFFFF"));
        AppActivity.getActionBarColorTheme().setCloseColor(Color.parseColor("#FFFFFF"));
        AppActivity.getActionBarColorTheme().setProgressColor(Color.parseColor("#FFFFFF"));

        // 创建广告View
        String adPlaceId = "3318891"; //  重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        adView = new AdView(this, adPlaceId);

        // 设置监听器
        adView.setListener(new AdViewListener() {
            public void onAdSwitch() {
                L.d("onAdSwitch");
            }

            public void onAdShow(JSONObject info) {
                // 广告已经渲染出来
                L.d("onAdShow " + info.toString());
            }

            public void onAdReady(AdView adView) {
                // 资源已经缓存完毕，还没有渲染出来
                L.d("onAdReady " + adView);
            }

            public void onAdFailed(String reason) {
                L.d("onAdFailed " + reason);
            }

            public void onAdClick(JSONObject info) {
                // L.d("onAdClick " + info.toString());
            }

            @Override
            public void onAdClose(JSONObject arg0) {
                L.d("onAdClose");
            }
        });
        // 将adView添加到父控件中(注：该父控件不一定为您的根控件，只要该控件能通过addView能添加广告视图即可)
        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rootLayout.addView(adView, rllp);

        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title_txt) {
                super.onReceivedTitle(view, title_txt);
            }
        };

        WebViewClient wvc = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                web.loadUrl(url);
                L.d("页面内跳转的url是====="+url);

                int index = url.indexOf("_");
                String temp=url.substring(index+1);
                String ads_id=temp.replace(".html","");
                id=Integer.valueOf(ads_id);
                loadCollectData(id);
                // 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
                return true;
            }
        };
        web.setWebViewClient(wvc);
        web.setWebChromeClient(wvcc);
        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        web.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
                        web.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setSupportMultipleWindows(false);
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //localstorage
//        web.getSettings().setDomStorageEnabled(true);
//        web.getSettings().setAppCacheMaxSize(1024*1024*8);
//        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
//        web.getSettings().setAppCachePath(appCachePath);
//        web.getSettings().setAllowFileAccess(true);
//
//        web.getSettings().setAppCacheEnabled(true);

        web.loadUrl(url);




    }



    private void loadCollectData(int ads_id){

        Map<String, Object> map = new HashMap<>();

        final ACache mCache = ACache.get(NewsDetailsActivity.this);

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

        map.put("news_id",ads_id);


        XUtil.Post(NewsDetailsActivity.this, AddressApi.CHECK_COLLECT, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                CheckCollectBean bean = JSON.parseObject(result,
                        CheckCollectBean.class);

                if ("200".equals(bean.getResult()) ){

                    collect_statue=bean.getData();

                    if("1".equals(bean.getData())){

                        collect_icon.setImageResource(R.mipmap.collect_yes);

                    }else if("0".equals(bean.getData())){

                        collect_icon.setImageResource(R.mipmap.collect_no);

                    }



                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(NewsDetailsActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                } else if("300".equals(bean.getResult())){

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(NewsDetailsActivity.this);

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
                Toast.makeText(NewsDetailsActivity.this, "服务异常", Toast.LENGTH_SHORT).show();

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
        MyAlertDialog dialog = new MyAlertDialog(NewsDetailsActivity.this)
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

                Intent login = new Intent(NewsDetailsActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }


    //增加收藏
    private void addCollectData(int ads_id){

        Map<String, Object> map = new HashMap<>();

        final ACache mCache = ACache.get(NewsDetailsActivity.this);

            map.put("user_id",mCache.getAsString("user_id"));
            map.put("userkey",mCache.getAsString("token"));
            map.put("news_id",ads_id);


        XUtil.Post(NewsDetailsActivity.this, AddressApi.ADD_COLLECT, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                AddDeleteCollectBean bean = JSON.parseObject(result,
                        AddDeleteCollectBean.class);

                if ("200".equals(bean.getResult()) ){
                    Toast.makeText(NewsDetailsActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                    collect_icon.setImageResource(R.mipmap.collect_yes);
                    collect_statue="1";

                    EventBus.getDefault().post(
                            new CollectType(""));

                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(NewsDetailsActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }else if("300".equals(bean.getResult())){

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(NewsDetailsActivity.this);

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
                Toast.makeText(NewsDetailsActivity.this, "服务异常", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });

    }


    //取消收藏
    private void deleteCollectData(int ads_id){

        Map<String, Object> map = new HashMap<>();

        final ACache mCache = ACache.get(NewsDetailsActivity.this);

        map.put("user_id",mCache.getAsString("user_id"));
        map.put("userkey",mCache.getAsString("token"));
        map.put("news_id",ads_id);


        XUtil.Post(NewsDetailsActivity.this, AddressApi.DELETE_COLLECT, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                AddDeleteCollectBean bean = JSON.parseObject(result,
                        AddDeleteCollectBean.class);

                if ("200".equals(bean.getResult()) ){
                    Toast.makeText(NewsDetailsActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                    collect_icon.setImageResource(R.mipmap.collect_no);

                    collect_statue="0";

                    EventBus.getDefault().post(
                            new CollectType(""));


                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(NewsDetailsActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }else if("300".equals(bean.getResult())){

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(NewsDetailsActivity.this);

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
                Toast.makeText(NewsDetailsActivity.this, "服务异常", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });

    }




    private boolean checkLoginStatue(){

        mLoginPreference = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        mIsLogin = mLoginPreference.getBoolean(KEY_IS_LOGIN, false);

        return mIsLogin;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.collect_layout:

                if("1".equals(collect_statue)){
                    //取消收藏
                    deleteCollectData(id);

                }else if("0".equals(collect_statue)){
                    //增加收藏
                    addCollectData(id);
                }

                break;

        }
    }


    @Override
    protected void onDestroy() {
        adView.destroy();
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
}
