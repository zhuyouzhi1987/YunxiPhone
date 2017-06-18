package com.yunxi.phone.activity;

import android.view.View;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by Administrator on 2017/1/23.
 */
public class DownloadInfoActivity extends BaseActivity implements View.OnClickListener {
    private AutoRelativeLayout back;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download_info;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
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
