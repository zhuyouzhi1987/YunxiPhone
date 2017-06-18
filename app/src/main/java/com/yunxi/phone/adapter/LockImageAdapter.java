package com.yunxi.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yunxi.phone.R;
import com.yunxi.phone.activity.LockSettingActivity;
import com.yunxi.phone.bean.LockListBean;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/1/24.
 */
public class LockImageAdapter  extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<LockListBean> mDatas;
    public LockImageAdapter(Context context, List<LockListBean> mDatas)
    {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.itme_lock_image, parent,
                    false);
            viewHolder.lock_image = (ImageView) convertView.findViewById(R.id.lock_image);
            viewHolder.lock_select = (ImageView) convertView.findViewById(R.id.lock_select);

            if(position==0){
                //默认图片
                viewHolder.lock_image.setImageResource(R.mipmap.lock_bg);
            }else{
                Glide.with(mContext).load(mDatas.get(position).getThumbnail_Img()).placeholder(R.mipmap.news_placeholder).into(viewHolder.lock_image);
            }
            //判断哪个id在使用中

            if(LockSettingActivity.LOCK_ID==(mDatas.get(position).getID())){
                viewHolder.lock_select.setVisibility(View.VISIBLE);
            }else{
                viewHolder.lock_select.setVisibility(View.GONE);
            }

            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public void addList(List<LockListBean> list) {
        this.mDatas.addAll(list);
        notifyDataSetChanged();
    }
    public void clear() {
       mDatas.clear();
    }

    private final class ViewHolder
    {
        ImageView lock_image;
        ImageView lock_select;
    }
}
