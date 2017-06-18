package com.yunxi.phone.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Administrator on 2017/1/20.
 */
public class GuideViewPagerAdapter extends PagerAdapter {
    //界面列表
    private List<View> views;

    public GuideViewPagerAdapter (List<View> views){
        this.views = views;
    }

    //删除界面
    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(views.get(arg1));
    }

    @Override
    public void finishUpdate(View arg0) {
        // TODO Auto-generated method stub

    }

    //获得当前界面数量
    @Override
    public int getCount() {
        if (views != null)
        {
            return views.size();
        }

        return 0;
    }


    //初始化arg1位置的界面
    @Override
    public Object instantiateItem(View arg0, int arg1) {
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        ((ViewPager) arg0).addView(views.get(arg1), 0,mLayoutParams);
        return views.get(arg1);
    }

    //判断是否由对象生成界面
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public Parcelable saveState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
        // TODO Auto-generated method stub

    }
}
