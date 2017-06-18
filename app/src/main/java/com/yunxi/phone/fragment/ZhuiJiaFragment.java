package com.yunxi.phone.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.activity.LoginActivity;
import com.yunxi.phone.adapter.ZhuijiaAdapter;
import com.yunxi.phone.bean.TaskBean;
import com.yunxi.phone.bean.TaskContentBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/20.
 */
public class ZhuiJiaFragment extends Fragment implements View.OnClickListener {
    private AutoLoadListView tiyan_ListView;
    private int tiyan_page = 1;
    private boolean tiyan_isLoadMore;
    private ArrayList<TaskContentBean> task_beans;
    private ArrayList<TaskContentBean> task = new ArrayList<>();
    private ZhuijiaAdapter zhuijia_adapter;
    ValueReceiver receiver;
    int downloadType = 0;
    public static TiyanFragment tiyanFragment = null;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    private AutoRelativeLayout data_zero;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tiyan_tab_layout, container, false);
        tiyan_ListView = (AutoLoadListView) view.findViewById(R.id.tiyan_listview);
//        EventBus.getDefault().register(this);
        loading = (AutoLinearLayout) view.findViewById(R.id.loading);
        not_data = (AutoRelativeLayout) view.findViewById(R.id.not_data);
        data_zero = (AutoRelativeLayout) view.findViewById(R.id.data_zero);
        get_data = (AutoRelativeLayout) view.findViewById(R.id.get_data);
        init();
        return view;
    }

    private void init() {

        mSharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        get_data.setOnClickListener(this);
        zhuijia_adapter = new ZhuijiaAdapter(getActivity());
        tiyan_ListView.setAdapter(zhuijia_adapter);
        tiyan_ListView.setHasMoreItems(false);

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
        map.put("type", "1");
        map.put("page", page);
        XUtil.Post(getActivity(), AddressApi.GET_USER_TASK, map, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                L.i("zhuijiaData=====" + result.toString());

                boolean hasMoreData = true;
                hideLoading();
                TaskBean bean = JSON.parseObject(result,
                        TaskBean.class);
                not_data.setVisibility(View.GONE);

                if ("200".equals(bean.getResult())) {

                    task_beans = bean.getData();

                    if (task_beans.size() > 0) {
                        for (TaskContentBean task_bean : task_beans) {
                            for (PackageInfo info : getActivity().getPackageManager().getInstalledPackages(0)) {
                                if (task_bean.getTask_Package_Name().equals(info.packageName)) {
                                    task.add(task_bean);
                                    break;
                                }
                            }
                        }

                        if(task.size()<=0){
                            data_zero.setVisibility(View.VISIBLE);
                        }
                        zhuijia_adapter.addAll(task);
                        zhuijia_adapter.notifyDataSetChanged();
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
                } else if ("300".equals(bean.getResult())) {

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(getActivity());

                    String phone = mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check", "0");
                    mCache.put("phone", "");

                    //激光注销掉
                    JPushInterface.setAlias(getActivity(), "0", new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });


                    showWarnDialog(phone);


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


    private void showWarnDialog(String phone){
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("您的账号"+phone+"已再其他设备登录 , 如非本人操作请重置密码 ! 确保账号安全 ！")
                .setTitle("系统提示")
                .setNegativeButton("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.setPositiveButton("重新登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent login = new Intent(getActivity(), LoginActivity.class);
                login.putExtra("from", "loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }

    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.pingfu.download");
//        receiver = new ValueReceiver();
//        getActivity().registerReceiver(receiver, filter);
//        if (ad != null) {
//            initData();
//        }
        zhuijia_adapter.notifyDataSetChanged();
        MobclickAgent.onPageStart("zhuijia");
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("zhuijia");
    }
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
    public void onDestroy() {
        super.onDestroy();
//        getActivity().unregisterReceiver(receiver);
//        EventBus.getDefault().unregister(this);
    }

    private class ValueReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            for(int i=0;i<task_beans.size();i++){
//                if(task_beans.get(i).getTask_ID()==intent.getIntExtra("task_id", 0)){
//                    downloadType = intent.getIntExtra("type", 0);
//                    //获得position
//                    zhuijia_adapter.updateItem(i,tiyan_ListView,downloadType,intent);
//                    break;
//                }
//            }
        }

    }
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}
