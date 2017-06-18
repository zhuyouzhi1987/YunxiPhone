package com.yunxi.phone.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunxi.phone.R;
import com.yunxi.phone.bean.IntegralBean;
import com.yunxi.phone.bean.SendAdsBean;
import com.yunxi.phone.bean.SendAdsContentBean;
import com.yunxi.phone.eventtype.RefershType;
import com.yunxi.phone.eventtype.ShareAdsType;
import com.yunxi.phone.utils.UserInfoUtil;
import com.zhy.autolayout.utils.AutoUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/10.
 */
public class SendAdapter extends ArrayListAdapter<SendAdsContentBean> {
    Context context;

    public SendAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.send_item, null);

            viewHolder.title=(TextView)convertView.findViewById(R.id.title);
            viewHolder.desc=(TextView)convertView.findViewById(R.id.desc);
            viewHolder.share=(TextView)convertView.findViewById(R.id.share);
            viewHolder.ads_icon=(ImageView)convertView.findViewById(R.id.icon);

            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SendAdsContentBean info = getItem(position);



        viewHolder.title.setText(info.getAdvertise_Title());
        viewHolder.share.setText("+"+info.getAdvertise_Price()+"云朵");
        viewHolder.desc.setText(info.getAdvertise_Demand());
        Glide.with(context).load(info.getAdvertise_Show_Icon()).centerCrop().dontAnimate().placeholder(R.mipmap.placeholder_square).into(viewHolder.ads_icon);

        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EventBus.getDefault().post(
                        new ShareAdsType(info.getAdvertise_ID(),info.getAdvertise_Url(),info.getAdvertise_Type(),info.getAdvertise_Content()));

            }
        });

        return convertView;
    }
    private static class ViewHolder {
        TextView title;
        TextView desc;
        TextView share;
        ImageView ads_icon;
    }






}
