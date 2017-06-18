package com.yunxi.phone.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.GuideViewPagerAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/20.
 */
public class GuideAcitivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private List<View> views;
    private ViewPager vp;
    GuideViewPagerAdapter vpAdapter;

    private Button button;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void findById() {
        button=$(R.id.button);
        vp=$(R.id.vp_guide);
    }

    @Override
    protected void regListener() {

    }

    @Override
    protected void init() {


        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        // 初始化引导图片列表
        views.add(inflater.inflate(R.layout.what_new_one, null));
        views.add(inflater.inflate(R.layout.what_new_two, null));
        views.add(inflater.inflate(R.layout.what_new_three, null));
        views.add(inflater.inflate(R.layout.what_new_four, null));

        // 初始化Adapter
        vpAdapter = new GuideViewPagerAdapter(views);

        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);

        vp.setPageMargin(0);
        // 初始化Adapter

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(GuideAcitivity.this, LoginActivity.class);
                intent.putExtra("from", "loading");
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 3) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    protected void setStatusBar() {

        StatusBarUtil.setTranslucentForImageView(GuideAcitivity.this, 0, null);
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
