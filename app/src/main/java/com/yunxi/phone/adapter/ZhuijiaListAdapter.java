package com.yunxi.phone.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import com.bumptech.glide.Glide;
import com.yunxi.phone.R;
import com.yunxi.phone.activity.DownloadTaskActivity;
import com.yunxi.phone.bean.OnlineGameContentBean;
import com.yunxi.phone.bean.TaskAddContentBean;
import com.yunxi.phone.bean.TaskContentBean;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by bond on 16/3/17.
 */
public class ZhuijiaListAdapter extends ArrayListAdapter<TaskAddContentBean> {
    Context context;

    public ZhuijiaListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.task_list_item, null);

            viewHolder.title=(TextView)convertView.findViewById(R.id.title);
            viewHolder.desc=(TextView)convertView.findViewById(R.id.desc);
            viewHolder.icon=(ImageView)convertView.findViewById(R.id.icon);
            viewHolder.task_btn=(TextView)convertView.findViewById(R.id.task_btn);

            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        final TaskAddContentBean info = getItem(position);


        viewHolder.title.setText(info.getTask_Name().toString());
        viewHolder.desc.setText(info.getTask_Content().toString());
        Glide.with(context).load(info.getTask_Icon()).centerCrop().placeholder(R.mipmap.placeholder_square).into(viewHolder.icon);


        if("1".equals(info.getTomorrow())){
            viewHolder.task_btn.setText("明日进行");
        }else if("0".equals(info.getTomorrow())){
            viewHolder.task_btn.setText("正常");
        }



        return convertView;
    }




    private static class ViewHolder {
        TextView title;
        TextView desc;
        ImageView icon;
        TextView task_btn;

    }


}
