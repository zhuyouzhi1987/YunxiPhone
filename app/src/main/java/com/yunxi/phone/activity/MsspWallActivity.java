package com.yunxi.phone.activity;

import android.view.View;
import android.widget.ImageView;

import com.baidu.mobads.appoffers.OffersManager;
import com.baidu.mobads.appoffers.OffersView;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by Administrator on 2017/1/19.
 */
public class MsspWallActivity extends BaseActivity implements View.OnClickListener{
    private AutoRelativeLayout back;
    private AutoRelativeLayout rl;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_wall;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        rl = $(R.id.rl);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
    }

    @Override
    protected void init() {
        OffersManager.showOffers(this);
        boolean showTitleBar=false;//如果为true，则显示页面顶端的bar；为false则不显示。
        OffersView ov=new OffersView(this, showTitleBar);
        rl.addView(ov);
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
