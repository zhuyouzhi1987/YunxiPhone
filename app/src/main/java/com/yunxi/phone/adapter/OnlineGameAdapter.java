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
import com.yunxi.phone.activity.WebActivity;
import com.yunxi.phone.bean.OnlineGameContentBean;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/30.
 */
public class OnlineGameAdapter extends BaseAdapter {
    private static final int TYPE_COUNT = 2;//item类型的总数
    private static final int TYPE_COMSUM = 1;//标题类型
    private static final int TYPE_CHARGE = 0;//内容类型
    Context context;

    private ArrayList<OnlineGameContentBean> lists=new ArrayList<OnlineGameContentBean>();


    public void clear() {
        lists.clear();
    }

    public ArrayList<OnlineGameContentBean> addAll(ArrayList<OnlineGameContentBean> content) {
        lists.addAll(content);

        return lists;
    }


    public OnlineGameAdapter(Context context) {
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
        View comsumView =null;
        View chargeView=null ;
        currentType = getItemViewType(position);

        if (currentType == TYPE_COMSUM) {

            TitleViewHolder titleViewHolder;
            //标题
            if (convertView == null) {
                titleViewHolder = new TitleViewHolder();
                comsumView = LayoutInflater.from(context).inflate(
                        R.layout.itme_title, null);
                titleViewHolder.iv = (ImageView) comsumView.findViewById(R.id.iv);
                titleViewHolder.single_layout=(AutoRelativeLayout)comsumView.findViewById(R.id.single_layout);
                comsumView.setTag(titleViewHolder);

                convertView = comsumView;

                AutoUtils.autoSize(convertView);
            } else {
                titleViewHolder = (TitleViewHolder) convertView.getTag();
            }


            Glide.with(context).load(lists.get(position).getGame_Cover()).centerCrop().dontAnimate().placeholder(R.mipmap.placeholder_banner).into(titleViewHolder.iv);
            titleViewHolder.single_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //处理点击

                    Intent i = new Intent(context, WebActivity.class);
                    i.putExtra("url",lists.get(position).getGame_Url() );
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });

        } else if (currentType == TYPE_CHARGE) {
            DescViewHolder chargeHoler ;
            if (convertView == null) {
                chargeHoler = new DescViewHolder();
                chargeView = LayoutInflater.from(context).inflate(
                        R.layout.itme_desc, null);
                chargeHoler.icon = (ImageView) chargeView.findViewById(R.id.icon);
                chargeHoler.title = (TextView) chargeView.findViewById(R.id.title);
                chargeHoler.desc = (TextView) chargeView.findViewById(R.id.desc);
                chargeHoler.double_layout=(AutoLinearLayout)chargeView.findViewById(R.id.double_layout);
                chargeView.setTag(chargeHoler);

                convertView = chargeView;

                AutoUtils.autoSize(convertView);
            } else {
                chargeHoler = (DescViewHolder) convertView.getTag();
            }

            Glide.with(context).load(lists.get(position).getGame_Icon()).centerCrop().dontAnimate().placeholder(R.mipmap.placeholder_square).into(chargeHoler.icon);

            chargeHoler.title.setText(lists.get(position).getGame_Name());
            chargeHoler.desc.setText(lists.get(position).getGame_Info());
            chargeHoler.double_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转
                    Intent i = new Intent(context, WebActivity.class);
                    i.putExtra("url",lists.get(position).getGame_Url() );
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }
            });
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;//总共有两个类型
    }

    @Override
    public int getItemViewType(int position) {
        if (lists.get(position).getIs_Important()==1) {
            return TYPE_COMSUM;// 标题类型
        } else if (lists.get(position).getIs_Important()==0) {
            return TYPE_CHARGE;// 详细类型
        } else {
            return 100;
        }
    }

    class TitleViewHolder {
        ImageView iv;
        AutoRelativeLayout single_layout;
    }

    class DescViewHolder {
        TextView title;
        TextView desc;
        ImageView icon;
        AutoLinearLayout double_layout;

    }
}
