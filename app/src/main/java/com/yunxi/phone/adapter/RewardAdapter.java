package com.yunxi.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunxi.phone.R;
import com.yunxi.phone.bean.RunningBaseBean;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by Administrator on 2017/1/10.
 */
public class RewardAdapter extends ArrayListAdapter<RunningBaseBean> {
    Context context;

    public RewardAdapter(Context context) {
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item_reward, null);

            viewHolder.point=(TextView)convertView.findViewById(R.id.point);
            viewHolder.desc=(TextView)convertView.findViewById(R.id.desc);


            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RunningBaseBean info = getItem(position);
        if(getCount()-1==position){
            //最后一个
            viewHolder.point.setBackgroundResource(R.drawable.red_circle);
            viewHolder.desc.setText(info.getRun_Steps() + "云朵可抵扣" + info.getRun_Integral() + "分钟国内主叫服务 。");
            viewHolder.desc.setTextColor(context.getResources().getColor((R.color.red)));
        }else{
            viewHolder.point.setBackgroundResource(R.drawable.defaule_circle);
            viewHolder.desc.setText("每日完成运动" + info.getRun_Steps() + "步" + " , " + "可获得" + info.getRun_Integral() + "云朵 !");
            viewHolder.desc.setTextColor(context.getResources().getColor((R.color.bottomTabTxtUnSelectColor)));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView point;
        TextView desc;
    }
}
