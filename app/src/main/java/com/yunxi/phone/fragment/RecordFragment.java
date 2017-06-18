package com.yunxi.phone.fragment;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.BuildConfig;
import com.yunxi.phone.R;
import com.yunxi.phone.activity.AddContactActivity;
import com.yunxi.phone.activity.CertificationActivity;
import com.yunxi.phone.activity.DailActivity;
import com.yunxi.phone.activity.LoginActivity;
import com.yunxi.phone.activity.UpDateContactActivity;
import com.yunxi.phone.adapter.RecordAdapter;
import com.yunxi.phone.bean.CallBean;
import com.yunxi.phone.bean.CallContentBean;
import com.yunxi.phone.bean.RecordBean;
import com.yunxi.phone.bean.SwipeMenu;
import com.yunxi.phone.eventtype.ChangeTabType;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.CallRecordUtils;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.SPUtils;
import com.yunxi.phone.utils.SingleButtonAlertDialog;
import com.yunxi.phone.utils.SwipeMenuCreator;
import com.yunxi.phone.utils.SwipeMenuItem;
import com.yunxi.phone.utils.SwipeMenuListView;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;


public class RecordFragment extends Fragment {

    private static final int UPDATE_CODE = 1000;
    private LayoutInflater mInflater;
    SwipeMenuListView listView;
    List<RecordBean> recordList = new ArrayList<RecordBean>();
    RecordAdapter adapter;
    AutoRelativeLayout clearn;
    AutoRelativeLayout syn;
    CallBean bean = null;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    private AutoLinearLayout loading;
    private TextView tv_desc;
    CallContentBean data;
    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;
    private boolean mIsLogin;
    ValueReceiver receiver;
    private SharedPreferences mLoginPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment, container, false);
        mInflater = LayoutInflater.from(getActivity());
        listView = (SwipeMenuListView) view.findViewById(R.id.listView);
        clearn = (AutoRelativeLayout) view.findViewById(R.id.clearn);
        syn = (AutoRelativeLayout) view.findViewById(R.id.syn);
        not_data = (AutoRelativeLayout) view.findViewById(R.id.not_data);
        get_data = (AutoRelativeLayout) view.findViewById(R.id.get_data);
        loading = (AutoLinearLayout) view.findViewById(R.id.loading);
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void loadData() {
        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(getActivity());

        if ("".equals(mCache.getAsString("user_id")) || mCache.getAsString("user_id") == null) {
            map.put("user_id", "0");
        } else {
            map.put("user_id", mCache.getAsString("user_id"));
        }

        if ("".equals(mCache.getAsString("token")) || mCache.getAsString("token") == null) {
            map.put("userkey", "0");
        } else {
            map.put("userkey", mCache.getAsString("token"));
        }

//        map.put("page", pager);

        XUtil.Post(getActivity(), AddressApi.CALL_CENTER, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                L.d("CALL_CENTER:++" + result.toString());
                bean = JSON.parseObject(result,
                        CallBean.class);

                if ("200".equals(bean.getResult())) {
                    //加载头部数据

                    data = bean.getData();

                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
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

                L.d("异常" + ex.toString());

                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void showWarnDialog(String phone) {
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("您的账号" + phone + "已再其他设备登录 , 如非本人操作请重置密码 ! 确保账号安全 ！")
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


    private void init() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!checkLoginStatue()){
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from","app_in");
                    startActivity(login);
                    return;
                }
                String phone = ACache.get(getActivity()).getAsString("phone");
                if(phone.startsWith("14") || phone.startsWith("17")){
                    showWarnPhoneDialog();
                    return;
                }
                if (null != data) {
                    if (TextUtils.isEmpty(data.getTalkminutes())) {
                        //跳转到验证
                        Intent intent = new Intent(getActivity(), CertificationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    int talkminutes = Integer.parseInt(data.getTalkminutes());
                    if (talkminutes >= 3 && talkminutes < 4) {
                        showTalkDialog(data.getColudnumber(), 3, recordList.get(position));
                    } else if (talkminutes >= 2 && talkminutes < 3) {
                        showTalkDialog(data.getColudnumber(), 2, recordList.get(position));
                    } else if (talkminutes >= 1 && talkminutes < 2) {
                        showTalkDialog(data.getColudnumber(), 1, recordList.get(position));
                    } else if (talkminutes == 0) {
                        showNotCloudDialog(recordList.get(position));
                    } else {
                        //打电话
                        if (!recordList.get(position).getNumber().startsWith("1")) {
                            //不能拨打
                            showSingleDialog();
                        } else {
                            if (recordList.get(position).getNumber().startsWith("14") || recordList.get(position).getNumber().startsWith("17")) {
                                //不能拨打
                                showSingleDialog();
                            } else {
                                //能拨打
                                Intent intent = new Intent(getActivity(), DailActivity.class);
                                intent.putExtra("name", recordList.get(position).getName());
                                intent.putExtra("phone", "+86" + recordList.get(position).getNumber());
                                intent.putExtra("formatPhone", getPhone(recordList.get(position).getNumber()));
                                intent.putExtra("talk", data.getTalkminutes());
                                startActivity(intent);
                                saveReacord(recordList.get(position));
                            }
                        }
                    }
                }
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;
                    case 1:
                        createMenu2(menu);
                        break;
                }
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getActivity());
                item1.setBackground(R.color.line2);
                item1.setWidth((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 75));
                item1.setIcon(R.mipmap.editor);
                item1.setTitle("编辑好友");
                item1.setTitleColor(Color.parseColor("#FFFFFF"));
                item1.setTitleSize(11);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getActivity());
                item2.setBackground(R.color.red);
                item2.setWidth((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 75));
                item2.setIcon(R.mipmap.item_delect);
                item2.setTitle("删除记录");
                item2.setTitleColor(Color.parseColor("#FFFFFF"));
                item2.setTitleSize(11);
                menu.addMenuItem(item2);
            }

            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getActivity());
                item1.setBackground(R.color.line2);
                item1.setWidth((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 75));
                item1.setIcon(R.mipmap.add_user);
                item1.setTitle("添加好友");
                item1.setTitleColor(Color.parseColor("#FFFFFF"));
                item1.setTitleSize(11);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getActivity());
                item2.setBackground(R.color.red);
                item2.setWidth((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 75));
                item2.setIcon(R.mipmap.item_delect);
                item2.setTitle("删除记录");
                item2.setTitleColor(Color.parseColor("#FFFFFF"));
                item2.setTitleSize(11);
                menu.addMenuItem(item2);
            }
        };

        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                RecordBean recordBean = recordList.get(position);
                switch (index) {
                    case 0:
                        if (recordBean.getIsExite()) {
                            //编辑
                            Intent intent = new Intent(getActivity(), UpDateContactActivity.class);
                            intent.putExtra("name", recordBean.getName());
                            intent.putExtra("phone", recordBean.getNumber().replace(" ", ""));
                            intent.putExtra("id", recordBean.getId());
                            intent.putExtra("note", recordBean.getNote());
                            intent.putExtra("position", position);
                            intent.putExtra("flag", 0);
                            startActivityForResult(intent, RecordFragment.UPDATE_CODE);
                        } else {
                            //新建
                            Intent intent = new Intent(getActivity(), AddContactActivity.class);
                            intent.putExtra("phone", recordBean.getNumber().replace(" ",""));
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                        break;
                    case 1:
//                      boolean isDelete = CallRecordUtils.deleteCallLog(getActivity(), recordBean.getNumber());
//                      if (!isDelete) {
//                           showPermissonDialog();
//                      }
                        recordList.remove(position);
                        adapter.notifyDataSetChanged();
                        //更新缓存
                        ACache mCache = ACache.get(getActivity());
                        List<RecordBean> record = JSON.parseArray(mCache.getAsString("record"), RecordBean.class);
                        record.remove(position);
                        String jsonList = JSON.toJSONString(record);
                        mCache.put("record", jsonList);
                        if (recordList.size() == 0) {
                            clearn.setVisibility(View.GONE);
                            not_data.setVisibility(View.VISIBLE);
                            SPUtils.put(getActivity(), "record_data", false);
                        }

                        break;
                }
                return false;
            }
        });

        clearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWarnDialog();

            }
        });

        get_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPermisson();
            }
        });

        syn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSynDialog();
            }
        });
    }

    private void saveReacord(RecordBean recordBean) {
        ACache mCache = ACache.get(getActivity());
        String recordjson = mCache.getAsString("record");
        List<RecordBean> recordList = JSON.parseArray(recordjson, RecordBean.class);

        RecordBean recor = new RecordBean();
        recor.setName(recordBean.getName());
        recor.setNumber(recordBean.getNumber());
        long time = System.currentTimeMillis();
        recor.setDate("");
        recor.setDatetime(time);
        recor.setIsExite(recordBean.getIsExite());
//        String contactID = new ContactsManager(getActivity(), getActivity().getContentResolver()).getContactID(recordBean.getName());
//        if (!contactID.equals("0")) {
//            //存在
//            String note = new ContactsManager(getActivity(), getActivity().getContentResolver()).getNote(recordBean.getName());
//            recor.setIsExite(true);
//            recor.setNote(note);
//        } else {
//            recor.setIsExite(false);
//        }
        if(!TextUtils.isEmpty(recordBean.getId())){
            recor.setId(recordBean.getId());
        }else{
            recor.setId("0");
        }
        recordList.add(0, recor);
        String recordjsonList = JSON.toJSONString(recordList);
        mCache.put("record", recordjsonList);
    }


    private float getRawSize(int unit, float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

    boolean isExchege = true;

    private void showSynDialog() {
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("将要与手机内手机通话记录互相同步 , 是否继续 ?")
                .setTitle("系统提示")
                .setNegativeButton("暂不同步", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.setPositiveButton("立即同步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                not_data.setVisibility(View.GONE);
                tv_desc.setText("正在同步通话记录...");
                MyTask mTask = new MyTask();
                mTask.execute();


            }
        });
        dialog.show();
    }
    private void showWarnPhoneDialog() {
        SingleButtonAlertDialog dialog = new SingleButtonAlertDialog(getActivity())
                .builder().setMsg("抱歉 ! 暂不支持14/17开头的手机号使用电话服务")
                .setTitle("系统提示")
                .setPositiveButton("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.show();
    }
    private void showWarnDialog() {
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("将要删除所有的通话记录 , 是否继续 ?")
                .setTitle("系统提示")
                .setNegativeButton("暂不删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.setPositiveButton("确认删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isDelete = CallRecordUtils.deleteCallAll(getActivity());
//                if (!isDelete) {
//                    showPermissonDialog();
//                }
                SPUtils.put(getActivity(), "record_data", false);
                recordList.clear();
                adapter.notifyDataSetChanged();
                not_data.setVisibility(View.VISIBLE);
                ACache mCache = ACache.get(getActivity());
                mCache.put("record", "[]");
                clearn.setVisibility(View.GONE);

            }
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction("change_record");

        receiver = new ValueReceiver();
        getActivity().registerReceiver(receiver, filter);
        MobclickAgent.onPageStart("recordfragment");
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("recordfragment");
    }
    private void initView() {
        ACache mCache = ACache.get(getActivity());
        String record = mCache.getAsString("record");
        if(TextUtils.isEmpty(record)){
            mCache.put("record","[]");
            record="[]";
        }
        if (!record.equals("[]")) {
            recordList = JSON.parseArray(record, RecordBean.class);
            not_data.setVisibility(View.GONE);
        } else {
            not_data.setVisibility(View.VISIBLE);
//            showPermissonDialog();
//            CallRecordUtils callRecord = new CallRecordUtils();
//            String dataList = callRecord.getDataList(getActivity());
//            recordList = JSON.parseArray(dataList, RecordBean.class);
        }


        if (record.equals("[]")) {
            //没有内容不显示垃圾箱
            clearn.setVisibility(View.GONE);
        } else {
            clearn.setVisibility(View.VISIBLE);
        }

        adapter = new RecordAdapter(getActivity(), recordList,listView);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
    private class ValueReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RecordFragment.UPDATE_CODE && resultCode == 0) {
            //处理缓存中的数据
            L.d(" 刷新通话记录列表回调");
            if (null != data) {
                String record = data.getExtras().getString("record");
                recordList = JSON.parseArray(record, RecordBean.class);
                adapter.setRecordList(recordList);
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == 100) {
            CallRecordUtils callRecord = new CallRecordUtils();
            callRecord.getDataList(getActivity());
        }
    }

    private void showPermissonDialog() {
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("与手机内置通讯录同步失败 ，请对朋友圈开放通讯录获取权限 ！")
                .setTitle("系统提示")
                .setNegativeButton("稍后再说", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.setPositiveButton("前往授权", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPermisson();
            }
        });

        dialog.show();
    }

    public void goPermisson() {
        if (Build.MANUFACTURER.equals("Meizu")) {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
//                goPermisson();
            }
        } else if (Build.MANUFACTURER.equals("Xiaomi")) {
            Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
            ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            i.setComponent(componentName);
            i.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
            try {
                startActivityForResult(i, 100);
            } catch (Exception e) {
                e.printStackTrace();
//                goPermisson();
            }
        } else if (Build.MANUFACTURER.equals("HUAWEI")) {
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
                intent.setComponent(comp);
                startActivityForResult(intent, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", "com.yunxi.phone", null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", "com.yunxi.phone");
            }
            startActivityForResult(localIntent, 100);
        }
    }


    private void showNotCloudDialog(final RecordBean bean) {
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("您账户内暂无可用通话时长 , 请完成任务获取云朵 , 10云朵=1分钟 ！")
                .setTitle("系统提示")
                .setNegativeButton("稍后再说", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        dialog.setPositiveButton("赚取云朵", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打电话
                EventBus.getDefault().post(
                        new ChangeTabType("cloud"));
            }
        });

        dialog.show();
    }

    private void showTalkDialog(String actualCloud, int min, final RecordBean bean) {
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("您账户中仅剩" + actualCloud + "云朵 , 可兑换通话" + min + "分钟 ! 完成简单任务即可获得海量云朵")
                .setTitle("系统提示")
                .setNegativeButton("赚取云朵", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(
                                new ChangeTabType("cloud"));
                    }
                });

        dialog.setPositiveButton("立即通话", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bean.getNumber().startsWith("1")) {
                    //不能拨打
                    showSingleDialog();
                } else {
                    if (bean.getNumber().startsWith("14") || bean.getNumber().startsWith("17")) {
                        //不能拨打
                        showSingleDialog();
                    } else {
                        //能拨打
                        Intent intent = new Intent(getActivity(), DailActivity.class);
                        intent.putExtra("name", bean.getName());
                        intent.putExtra("phone", "+86" + bean.getNumber());
                        intent.putExtra("formatPhone", getPhone(bean.getNumber()));
                        intent.putExtra("talk", data.getTalkminutes());
                        startActivity(intent);
                        saveReacord(bean);
                    }
                }
            }
        });

        dialog.show();
    }

    private void showSingleDialog() {
        SingleButtonAlertDialog dialog = new SingleButtonAlertDialog(getActivity())
                .builder().setMsg("目前仅支持拨打手机号码（新疆/西藏号码及14/17号段暂不支持）")
                .setTitle("系统提示")
                .setPositiveButton("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        dialog.show();
    }

    public String getPhone(String phone) {
        if (phone.startsWith("1")) {
            StringBuilder sb = new StringBuilder();
            sb.append(phone);
            for (int i = 0; i < sb.length(); i++) {
                if (i == 3) {
                    sb.insert(3, " ");
                } else if (i == 8) {
                    sb.insert(8, " ");
                }
            }
            L.d(sb.toString());
            return sb.toString();
        } else {
            return phone;
        }
    }


    private class MyTask extends AsyncTask<String, Integer, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {

        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            //手机数据
            CallRecordUtils callRecord = new CallRecordUtils();
            String callRecordJson = callRecord.getDataList(getActivity());
            List<RecordBean> phoneRecorList = JSON.parseArray(callRecordJson, RecordBean.class);

            //本地数据
            ACache mCache = ACache.get(getActivity());
            String locationRecordJson = mCache.getAsString("record");
            List<RecordBean> locationRecordList = JSON.parseArray(locationRecordJson, RecordBean.class);

            for (RecordBean phoneRecord : locationRecordList) {
                isExchege = true;
                for (RecordBean locationRecord : phoneRecorList) {
                    if (phoneRecord.getDatetime() == locationRecord.getDatetime()) {
                        isExchege = false;
                        continue;
                    }
                }
                if (isExchege) {
                    //添加到手机数据库
                    callRecord.insertCallLog(getActivity(), phoneRecord.getName(), phoneRecord.getNumber(), phoneRecord.getDatetime());
                }
            }

            String data = callRecord.getDataList(getActivity());
            mCache.put("record", data);
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {

        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            initView();
            loading.setVisibility(View.GONE);
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
    private boolean checkLoginStatue(){

        mLoginPreference = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        mIsLogin = mLoginPreference.getBoolean(KEY_IS_LOGIN, false);

        return mIsLogin;
    }


}
