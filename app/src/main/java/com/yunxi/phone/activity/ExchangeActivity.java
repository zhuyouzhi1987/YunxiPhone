package com.yunxi.phone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.fragment.HuafeiFragment;
import com.yunxi.phone.fragment.QbFragment;
import com.yunxi.phone.utils.StatusBarUtil;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/19.
 */
public class ExchangeActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private AutoRelativeLayout back;
    private View huafei_tabView, qb_tabView;
    private ViewPager mPager;
    private TextView tiyan_btn, zhuijia_btn;
    private AutoRelativeLayout tips_btn;
    private List<Fragment> tabList = new ArrayList<Fragment>();
    private FragmentStatePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        mPager = (ViewPager) findViewById(R.id.pager);
        back = (AutoRelativeLayout) findViewById(R.id.back);
        tiyan_btn = (TextView) findViewById(R.id.tiyan_btn);
        zhuijia_btn = (TextView) findViewById(R.id.zhuijia_btn);
        tips_btn = (AutoRelativeLayout) findViewById(R.id.tips);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.appMainColor));
        init();

    }


    protected void init() {
        back.setOnClickListener(this);
        tiyan_btn.setOnClickListener(this);
        zhuijia_btn.setOnClickListener(this);
        tips_btn.setOnClickListener(this);
        HuafeiFragment huafeiFragment = new HuafeiFragment();
        QbFragment qbFragment = new QbFragment();
//        tabList.add(huafeiFragment);
        tabList.add(qbFragment);

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

        mPager.setOnPageChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tiyan_btn:
                mPager.setCurrentItem(0);
                break;
            case R.id.zhuijia_btn:
                mPager.setCurrentItem(1);
                break;
            case R.id.tips:
                Intent intent = new Intent(this,ExchangeRecord.class);
                startActivity(intent);
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
                break;
            case 1:
                zhuijia_btn.setBackgroundResource(R.drawable.green_yuanjiao);
                zhuijia_btn.setTextColor(getResources().getColor(R.color.white));
                tiyan_btn.setBackgroundResource(R.drawable.white_yuanjiao);
                tiyan_btn.setTextColor(getResources().getColor(R.color.appMainColor));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
