package com.yunxi.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunxi.phone.R;
import com.yunxi.phone.bean.IntegralBean;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by Administrator on 2017/1/10.
 */
public class SportAdapter extends ArrayListAdapter<IntegralBean> {
    Context context;

    public SportAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item_run_info, null);

            viewHolder.time=(TextView)convertView.findViewById(R.id.time);
            viewHolder.yundong_num=(TextView)convertView.findViewById(R.id.yundong_num);
            viewHolder.intergral=(TextView)convertView.findViewById(R.id.intergral);

            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final IntegralBean info = getItem(position);

        viewHolder.time.setText(info.getCreateTime());
        viewHolder.yundong_num.setText(info.getSteps());
        viewHolder.intergral.setText("+"+info.getIntegral()+"云朵");

        return convertView;
    }
    private static class ViewHolder {
        TextView time;
        TextView yundong_num;
        TextView intergral;
    }
}
