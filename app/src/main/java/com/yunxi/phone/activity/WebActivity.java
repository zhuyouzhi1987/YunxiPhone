package com.yunxi.phone.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.StatusBarUtil;


public class WebActivity extends BaseActivity {
    private WebView web;

    private String url;

    @Override
    protected void findById() {

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        web = $(R.id.web);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.game_web;
    }

    @Override
    protected void regListener() {

    }

    @Override
    protected void init() {

       url= getIntent().getStringExtra("url");

        initWebview();
    }

    private void initWebview() {

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
                if (!url.startsWith("weixin")) {
                    web.loadUrl(url);
                } else {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }

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
        //localstorage
        web.getSettings().setDomStorageEnabled(true);

        web.getSettings().setDatabaseEnabled(true);//支持database

        web.getSettings().setAppCacheMaxSize(1024*1024*8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        web.getSettings().setAppCachePath(appCachePath);
        web.getSettings().setAllowFileAccess(true);

        web.getSettings().setAppCacheEnabled(true);

        web.loadUrl(url);

    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucent(WebActivity.this);
    }
}
