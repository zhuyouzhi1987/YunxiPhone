package com.yunxi.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunxi.phone.R;
import com.yunxi.phone.bean.JifenDetailsBean;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by bond on 16/3/17.
 */
public class CloudDetailsAdapter extends ArrayListAdapter<JifenDetailsBean> {
    Context context;

    public CloudDetailsAdapter(Context context) {
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.jifen_details_item, null);

            viewHolder.time=(TextView)convertView.findViewById(R.id.time);
            viewHolder.jifen=(TextView)convertView.findViewById(R.id.jifen_details);

            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final JifenDetailsBean info = getItem(position);

        viewHolder.time.setText(info.getTime());
        viewHolder.jifen.setText(info.getDesc());

        return convertView;
    }

    private static class ViewHolder {
        TextView time;
        TextView jifen;
    }

}
