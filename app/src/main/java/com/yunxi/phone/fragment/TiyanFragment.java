package com.yunxi.phone.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.TiyanListAdapter;
import com.yunxi.phone.bean.TaskBean;
import com.yunxi.phone.bean.TaskContentBean;
import com.yunxi.phone.eventtype.StepType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/20.
 */
public class TiyanFragment extends Fragment implements View.OnClickListener {
    private AutoLoadListView tiyan_ListView;
    private int tiyan_page = 1;
    private boolean tiyan_isLoadMore;
    private ArrayList<TaskContentBean> task_beans;
    private TiyanListAdapter tiyan_adapter;
    ValueReceiver receiver;
    int downloadType = 0;
    public static TiyanFragment tiyanFragment = null;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    private AutoRelativeLayout data_zero;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_data:
                showLoading();
                tiyanData(1);
                break;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tiyan_tab_layout, container, false);
        tiyan_ListView = (AutoLoadListView) view.findViewById(R.id.tiyan_listview);
        loading = (AutoLinearLayout) view.findViewById(R.id.loading);
        not_data = (AutoRelativeLayout) view.findViewById(R.id.not_data);
        get_data = (AutoRelativeLayout) view.findViewById(R.id.get_data);
        data_zero = (AutoRelativeLayout) view.findViewById(R.id.data_zero);

        EventBus.getDefault().register(this);
        init();
        return view;
    }

    private void init() {
        tiyan_adapter = new TiyanListAdapter(getActivity());
        tiyan_ListView.setAdapter(tiyan_adapter);
        tiyan_ListView.setHasMoreItems(false);
        get_data.setOnClickListener(this);
        tiyan_ListView.setPagingableListener(new AutoLoadListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                tiyan_isLoadMore = true;
                tiyanData(tiyan_page);
            }
        });

        tiyan_ListView.setBackTopListener(new AutoLoadListView.BackTop() {
            @Override
            public void onBackTop(int state) {

            }
        });

        showLoading();
        tiyanData(tiyan_page);
    }

    //加载体验数据
    private void tiyanData(final int page) {
        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(getActivity());
        L.d("uid====" + mCache.getAsString("user_id"));

        map.put("user_id", mCache.getAsString("user_id"));

        map.put("userkey", mCache.getAsString("token"));
        map.put("type", "0");
        map.put("page", page);
        XUtil.Post(getActivity(), AddressApi.GET_USER_TASK, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                L.i("下载列表结果=====" + result.toString());
                hideLoading();
                boolean hasMoreData = true;
                not_data.setVisibility(View.GONE);
                TaskBean bean = JSON.parseObject(result,
                        TaskBean.class);


                if ("200".equals(bean.getResult())) {

                    task_beans = bean.getData();
                    if (task_beans.size() > 0) {
                        tiyan_adapter.addAll(task_beans);
                        tiyan_adapter.notifyDataSetChanged();

                        tiyan_ListView.setIsLoading(false);

                        tiyan_ListView.setHasMoreItems(false);
                        tiyan_page = tiyan_page + 1;
                    } else {
                        if (page == 1) {
                            data_zero.setVisibility(View.VISIBLE);
                        }

                        hasMoreData = false;
                        tiyan_ListView.setIsLoading(false);
                        tiyan_ListView.setHasMoreItems(hasMoreData);
                    }
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
                    tiyan_ListView.setIsLoading(false);
                    tiyan_ListView.setHasMoreItems(hasMoreData);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                not_data.setVisibility(View.VISIBLE);
                hideLoading();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                hideLoading();
            }

            @Override
            public void onFinished() {
                hideLoading();
            }
        });
    }

    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.pingfu.download");
        receiver = new ValueReceiver();
        getActivity().registerReceiver(receiver, filter);
        tiyan_adapter.notifyDataSetChanged();
        MobclickAgent.onPageStart("tiyan");
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("tiyan");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
    }

    private class ValueReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(null==task_beans){
                return;
            }
            for (int i = 0; i < task_beans.size(); i++) {
                if (task_beans.get(i).getTask_ID() == intent.getIntExtra("task_id", 0)) {
                    downloadType = intent.getIntExtra("type", 0);
                    //获得position
                    tiyan_adapter.updateItem(i, tiyan_ListView, downloadType, intent);
                    break;
                }
            }
        }

    }

    public void onEventMainThread(StepType event) {
        tiyan_adapter.notifyDataSetChanged();
    }
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}
