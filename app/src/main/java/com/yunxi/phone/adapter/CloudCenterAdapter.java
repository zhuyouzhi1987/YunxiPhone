package com.yunxi.phone.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunxi.phone.R;
import com.yunxi.phone.activity.NewsDetailsActivity;
import com.yunxi.phone.bean.NewsContentBean;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

/**
 * Created by bond on 16/3/17.
 */
public class CloudCenterAdapter extends BaseAdapter {
    private static final int TYPE_COUNT = 2;//item类型的总数
    private static final int TYPE_SMALL = 0;//小图模式
    private static final int TYPE_THRID = 1;//3张图模式
    Context context;

    private ArrayList<NewsContentBean> lists=new ArrayList<NewsContentBean>();


    public void clear() {
        lists.clear();
    }

    public ArrayList<NewsContentBean> addAll(ArrayList<NewsContentBean> content) {
        lists.addAll(content);

        return lists;
    }


    public CloudCenterAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    int currentType;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View smallView=null ;
        View thirdView=null;
        currentType = getItemViewType(position);

        if (currentType == TYPE_SMALL) {
            SmallViewHolder smallViewHoler ;
            if (convertView == null) {
                smallViewHoler = new SmallViewHolder();
                smallView = LayoutInflater.from(context).inflate(
                        R.layout.news_item_small, null);
                smallViewHoler.news_small_icon = (ImageView) smallView.findViewById(R.id.news_small_icon);
                smallViewHoler.small_title = (TextView) smallView.findViewById(R.id.small_title);
                smallViewHoler.from = (TextView) smallView.findViewById(R.id.from);
                smallViewHoler.time = (TextView) smallView.findViewById(R.id.time);
                smallViewHoler.news_small_layout=(AutoRelativeLayout)smallView.findViewById(R.id.news_small_layout);
                smallView.setTag(smallViewHoler);

                convertView = smallView;

                AutoUtils.autoSize(convertView);
            } else {
                smallViewHoler = (SmallViewHolder) convertView.getTag();
            }

            Glide.with(context).load(lists.get(position).getN_imageurl1()).centerCrop().dontAnimate().placeholder(R.mipmap.news_placeholder).into(smallViewHoler.news_small_icon);

            smallViewHoler.small_title.setText(lists.get(position).getN_title());
            smallViewHoler.from.setText(lists.get(position).getN_source());
            smallViewHoler.time.setText(lists.get(position).getN_releaseTime());

            smallViewHoler.news_small_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转
                    Intent i = new Intent(context, NewsDetailsActivity.class);
                    i.putExtra("url",lists.get(position).getMy_url());
                    i.putExtra("ads_id",lists.get(position).getN_id());

                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }
            });
        }else if(currentType==TYPE_THRID){

            ThirdViewHolder thirdViewHolder;
            if(convertView==null){
                thirdViewHolder=new ThirdViewHolder();
                thirdView=LayoutInflater.from(context).inflate(R.layout.news_item_third,null);

                thirdViewHolder.third_title=(TextView)thirdView.findViewById(R.id.third_title);
                thirdViewHolder.third_one_bg=(ImageView)thirdView.findViewById(R.id.third_one_bg);
                thirdViewHolder.third_two_bg=(ImageView)thirdView.findViewById(R.id.third_two_bg);
                thirdViewHolder.third_three_bg=(ImageView)thirdView.findViewById(R.id.third_three_bg);

                thirdViewHolder.from=(TextView)thirdView.findViewById(R.id.from);
                thirdViewHolder.time=(TextView)thirdView.findViewById(R.id.time);

                thirdViewHolder.news_third_layout=(AutoRelativeLayout)thirdView.findViewById(R.id.news_third_layout);
                thirdView.setTag(thirdViewHolder);

                convertView=thirdView;

                AutoUtils.autoSize(convertView);

            }else{
                thirdViewHolder=(ThirdViewHolder)convertView.getTag();
            }


            thirdViewHolder.third_title.setText(lists.get(position).getN_title());
            thirdViewHolder.from.setText(lists.get(position).getN_source());
            thirdViewHolder.time.setText(lists.get(position).getN_releaseTime());


            Glide.with(context).load(lists.get(position).getN_imageurl1()).centerCrop().dontAnimate().placeholder(R.mipmap.news_placeholder).into(thirdViewHolder.third_one_bg);
            Glide.with(context).load(lists.get(position).getN_imageurl2()).centerCrop().dontAnimate().placeholder(R.mipmap.news_placeholder).into(thirdViewHolder.third_two_bg);
            Glide.with(context).load(lists.get(position).getN_imageurl3()).centerCrop().dontAnimate().placeholder(R.mipmap.news_placeholder).into(thirdViewHolder.third_three_bg);

            thirdViewHolder.news_third_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, NewsDetailsActivity.class);
                    i.putExtra("url",lists.get(position).getMy_url());
                    i.putExtra("ads_id",lists.get(position).getN_id());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });

        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;//总共有三个类型
    }

    @Override
    public int getItemViewType(int position) {



        if (lists.get(position).getN_isImage()==2) {
            return TYPE_SMALL;// 小图模式
        } else if(lists.get(position).getN_isImage()==3){
            return TYPE_THRID;//三张图模式
        }else {
            return 100;//数据正常情况下，不会返回这种情况
        }

    }

    class BigViewHolder {
        ImageView big_news_bg;
        TextView big_title;
        TextView from;
        TextView time;
        AutoRelativeLayout news_big_layout;
    }

    class SmallViewHolder {
        TextView small_title;
        ImageView news_small_icon;
        TextView from;
        TextView time;
        AutoRelativeLayout news_small_layout;

    }

    class ThirdViewHolder{


        TextView third_title;
        ImageView third_one_bg,third_two_bg,third_three_bg;
        TextView from;
        TextView time;
        AutoRelativeLayout news_third_layout;

    }
}
