package com.yunxi.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunxi.phone.R;
import com.yunxi.phone.bean.SystemMsgBean;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by Administrator on 2017/1/13.
 */
public class SyetemMsgAdapter extends ArrayListAdapter<SystemMsgBean> {

    Context context;

    public SyetemMsgAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item_system_msg, null);

            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);

            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SystemMsgBean info = getItem(position);
        viewHolder.title.setText(info.getNotice_Title());
        viewHolder.time.setText(info.getCreateDate());
        viewHolder.desc.setText(info.getNotice_Info());
        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView time;
        TextView desc;
    }
}
