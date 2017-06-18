package com.yunxi.phone.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yunxi.phone.R;
import com.yunxi.phone.bean.RecordBean;
import com.yunxi.phone.utils.SwipeMenuListView;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/6.
 */
public class RecordAdapter extends BaseSwipListAdapter {
    private Context context;
    List<RecordBean> recordList;
    private LayoutInflater mInflater;
    String date;
    SwipeMenuListView sortListView;
    public RecordAdapter(FragmentActivity activity, List<RecordBean> recordList, SwipeMenuListView listView) {
        mInflater = LayoutInflater.from(activity);
        this.context=activity;
        this.recordList=recordList;
        this.sortListView = listView;
    }



    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        RecordBean recordBean = recordList.get(position);
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_record, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            viewHolder.tv_addr = (TextView) convertView
                    .findViewById(R.id.tv_addr);
            viewHolder.tv_time = (TextView) convertView
                    .findViewById(R.id.tv_time);
            viewHolder.more = (ImageView) convertView
                    .findViewById(R.id.more);

            viewHolder.record_item_layout=(AutoLinearLayout)convertView.findViewById(R.id.record_item_layout);

            AutoUtils.autoSize(convertView);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        viewHolder.mTextView.setText();
//        if(recordBean.getTime()>=2){
//            viewHolder.tv_name.setText(recordBean.getName()+"("+recordBean.getTime()+")");
//        }else{

//        }
        if(!TextUtils.isEmpty(recordBean.getName())){
            viewHolder.tv_name.setText(recordBean.getName());
        }else{
            viewHolder.tv_name.setText(recordBean.getNumber());
        }
        viewHolder.tv_addr.setText(recordBean.getAddr());
        if(isToday(new Date(recordBean.getDatetime()))){

            date = new SimpleDateFormat("HH:mm").format(new Date(recordBean.getDatetime()));
        }else{
            date = new SimpleDateFormat("MM-dd HH:mm").format(new Date(recordBean.getDatetime()));
        }
        viewHolder.tv_time.setText(date);
        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortListView.smoothOpenMenu(position);
            }
        });
        return convertView;
    }


    private final class ViewHolder
    {
        TextView tv_name;
        TextView tv_addr;
        TextView tv_time;
        ImageView more;

        AutoLinearLayout record_item_layout;
    }
    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        if(recordList.get(position).getIsExite()){
            //有这个用户 编辑页面
            return 0;
        }else{
            //有这个用户 新建页面
            return 1;
        }
    }

    public  void updataItem(int position,ListView mListView,Boolean flag){
        int firstvisible = mListView.getFirstVisiblePosition();
        int lastvisibale = mListView.getLastVisiblePosition();
        if(position>=firstvisible&&position<=lastvisibale){
            View view = mListView.getChildAt(position - firstvisible);
            ImageView more = (ImageView) view.findViewById(R.id.more);
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            //然后使用viewholder去更新需要更新的view。
            if(flag){

                more.setVisibility(View.INVISIBLE);
            }else{
                more.setVisibility(View.VISIBLE);
            }

        }

    }
    public void setRecordList(List<RecordBean> recordList){
        this.recordList=recordList;
    }


    public void updateItem(int index,ListView mListView,boolean flag )
    {
        if (mListView == null)
        {
            return;
        }

        // 获取当前可以看到的item位置
        int visiblePosition = mListView.getFirstVisiblePosition();
        // 如添加headerview后 firstview就是hearderview
        // 所有索引+1 取第一个view
        // View view = listview.getChildAt(index - visiblePosition + 1);
        // 获取点击的view
        View view = mListView.getChildAt(index - visiblePosition);
        ImageView more = (ImageView) view.findViewById(R.id.more);
        if(more.getVisibility()==View.GONE){
            more.setVisibility(View.VISIBLE);
        }else{
            more.setVisibility(View.GONE);
        }

    }
    public static boolean isToday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH)+1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH)+1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        if(year1 == year2 && month1 == month2 && day1 == day2){
            return true;
        }
        return false;
    }
}
