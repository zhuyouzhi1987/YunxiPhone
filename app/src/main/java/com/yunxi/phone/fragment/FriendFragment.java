package com.yunxi.phone.fragment;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.yunxi.phone.adapter.SortAdapter;
import com.yunxi.phone.bean.CallBean;
import com.yunxi.phone.bean.CallContentBean;
import com.yunxi.phone.bean.Contact;
import com.yunxi.phone.bean.RecordBean;
import com.yunxi.phone.bean.SortModel;
import com.yunxi.phone.bean.SwipeMenu;
import com.yunxi.phone.eventtype.ChangeTabType;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.CharacterParser;
import com.yunxi.phone.utils.ClearEditText;
import com.yunxi.phone.utils.ContactsManager;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.PinyinComparator;
import com.yunxi.phone.utils.SingleButtonAlertDialog;
import com.yunxi.phone.utils.SwipeMenuCreator;
import com.yunxi.phone.utils.SwipeMenuItem;
import com.yunxi.phone.utils.SwipeMenuListView;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;


public class FriendFragment extends Fragment implements OnClickListener {


    private LayoutInflater mInflater;
    private SwipeMenuListView sortListView;
    private ClearEditText mClearEditText;
    private AutoRelativeLayout add;
    private AutoRelativeLayout syn;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    Receiver receiver;
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
    TextView tv_desc;
    private PinyinComparator pinyinComparator;
    private SortAdapter adapter;
    CallBean bean = null;
    private AutoLinearLayout loading;
    private boolean mIsLogin;

    private SharedPreferences mLoginPreference;
    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;
    CallContentBean data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_fragment, container, false);
        mInflater = LayoutInflater.from(getActivity());
        sortListView = (SwipeMenuListView) view.findViewById(R.id.country_lvcountry);
        mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);
        add = (AutoRelativeLayout) view.findViewById(R.id.add);
        syn = (AutoRelativeLayout) view.findViewById(R.id.syn);
        not_data = (AutoRelativeLayout) view.findViewById(R.id.not_data);
        get_data = (AutoRelativeLayout) view.findViewById(R.id.get_data);
        loading = (AutoLinearLayout) view.findViewById(R.id.loading);
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction("action.denied");
        receiver = new Receiver();
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);


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

                L.d(result.toString());
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

    private void initView() {
        initList();
        getActivity().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
        getPhoneContacts();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

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
                    SortModel sortModel = adapter.getdateList().get(position);
                    int talkminutes = Integer.parseInt(data.getTalkminutes());
                    if (talkminutes >= 3 && talkminutes < 4) {
                        showTalkDialog(data.getColudnumber(), 3, sortModel);
                    } else if (talkminutes >= 2 && talkminutes < 3) {
                        showTalkDialog(data.getColudnumber(), 2, sortModel);
                    } else if (talkminutes >= 1 && talkminutes < 2) {
                        showTalkDialog(data.getColudnumber(), 1, sortModel);
                    } else if (talkminutes == 0) {
                        showNotCloudDialog(sortModel);
                    } else {
                        if (!sortModel.getPhone().startsWith("1")) {
                            //不能拨打
                            showSingleDialog();
                        } else {
                            if (sortModel.getPhone().startsWith("14") || sortModel.getPhone().startsWith("17")) {
                                //不能拨打
                                showSingleDialog();
                            } else {
                                //能拨打
                                Intent intent = new Intent(getActivity(), DailActivity.class);
                                intent.putExtra("name", sortModel.getName());
                                intent.putExtra("phone", "+86" + sortModel.getPhone());
                                intent.putExtra("formatPhone", getPhone(sortModel.getPhone()));
                                intent.putExtra("talk", data.getTalkminutes());
                                startActivity(intent);

                                saveReacord(sortModel.getName(), sortModel.getPhone(),sortModel.getNote(),sortModel.getId());
                            }
                        }
                    }

                }
            }
        });
        SourceDateList = filledData(contactList);
        Collections.sort(SourceDateList, pinyinComparator);
        //重置集合
        for (int x = 0; x < SourceDateList.size() - 1; x++) {
            if (x == 0) {
                SortModel sortModel = new SortModel();
                sortModel.setSortLetters(SourceDateList.get(x).getSortLetters().charAt(0) + "");
                sortModel.setTag("1");
                SourceDateList.add(x, sortModel);
                continue;
            }
            if (SourceDateList.get(x).getSortLetters().charAt(0) != SourceDateList.get(x + 1).getSortLetters().charAt(0)) {
                SortModel sortModel = new SortModel();
                sortModel.setSortLetters(SourceDateList.get(x + 1).getSortLetters().charAt(0) + "");
                sortModel.setTag("1");
                SourceDateList.add(x + 1, sortModel);
            }
        }


        adapter = new SortAdapter(getActivity(), SourceDateList,sortListView);
        sortListView.setAdapter(adapter);

        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });

        syn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //同步
                showWarnDialog();
                //请求数据
            }
        });

        get_data.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goPermisson();
            }
        });
    }

    private void showNotCloudDialog(final SortModel bean) {
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
                EventBus.getDefault().post(
                        new ChangeTabType("cloud"));

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
    private void showTalkDialog(String actualCloud, int min, final SortModel bean) {
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("您账户中仅剩" + actualCloud + "云朵 , 可兑换通话" + min + "分钟 ! 完成简单任务即可获得海量云朵")
                .setTitle("系统提示")
                .setNegativeButton("赚取云朵", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

        dialog.setPositiveButton("立即通话", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打电话
                if (!bean.getPhone().startsWith("1")) {
                    //不能拨打
                    showSingleDialog();
                } else {
                    if (bean.getPhone().startsWith("14") || bean.getPhone().startsWith("17")) {
                        //不能拨打
                        showSingleDialog();
                    } else {
                        //能拨打
                        Intent intent = new Intent(getActivity(), DailActivity.class);
                        intent.putExtra("name", bean.getName());
                        intent.putExtra("phone", "+86" + bean.getPhone());
                        intent.putExtra("formatPhone", getPhone(bean.getPhone()));
                        intent.putExtra("talk", data.getTalkminutes());
                        startActivity(intent);
                        saveReacord(bean.getName(), bean.getPhone(),bean.getNote(),bean.getId());
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

    String recordId;
    private void initList() {


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;
                    case 1:
                        //标题
//                        createMenu2(menu);
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
                item2.setTitle("删除好友");
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
                item2.setTitle("删除好友");
                item2.setTitleColor(Color.parseColor("#FFFFFF"));
                item2.setTitleSize(11);
                menu.addMenuItem(item2);
            }
        };

        sortListView.setMenuCreator(creator);
        sortListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                SortModel sortModel = SourceDateList.get(position);
                SortModel sortModel = adapter.getdateList().get(position);
                switch (index) {
                    case 0:
                        //编辑
                        Intent intent = new Intent(getActivity(), UpDateContactActivity.class);
                        intent.putExtra("name", sortModel.getName());
                        intent.putExtra("phone", sortModel.getPhone());
                        intent.putExtra("id", sortModel.getId());
                        intent.putExtra("note", sortModel.getNote());
                        intent.putExtra("position", position);
                        intent.putExtra("flag", 1);
                        startActivity(intent);
                        break;
                    case 1:
                        //删除
                        if (position == SourceDateList.size() - 1 && !TextUtils.isEmpty(SourceDateList.get(position - 1).getTag())) {
                            SourceDateList.remove(position);
                            SourceDateList.remove(position - 1);
                        } else {
                            if (SourceDateList.get(position).getSortLetters().equals(SourceDateList.get(position - 1).getSortLetters()) && !TextUtils.isEmpty(SourceDateList.get(position - 1).getTag()) && !TextUtils.isEmpty(SourceDateList.get(position + 1).getTag())) {
                                SourceDateList.remove(position);
                                SourceDateList.remove(position - 1);
                            } else {
                                SourceDateList.remove(position);
                            }
                        }

                        adapter.notifyDataSetChanged();

                        //操作手机数据库
//                        ContactsManager cm = new ContactsManager(getActivity(), getActivity().getContentResolver());
//                        cm.deleteContact(sortModel.getId());
                        ACache mCache = ACache.get(getActivity());
                        String contact = mCache.getAsString("contact");
                        List<Contact> cacheList = JSON.parseArray(contact, Contact.class);
//                        for (Contact contactBean : cacheList) {
//
//                        }
                        for(int x=0;x<cacheList.size();x++){
                            if (cacheList.get(x).getId().equals(sortModel.getId())) {
                                recordId=cacheList.get(x).getId();
                                cacheList.remove(x);
                            }
                        }
                        String jsonList = JSON.toJSONString(cacheList);
                        mCache.put("contact", jsonList);


                        //操作通讯撸
                        String recordjson = mCache.getAsString("record");
                        List<RecordBean> recordList = JSON.parseArray(recordjson, RecordBean.class);
                        for (RecordBean recordBean : recordList) {
                            if(recordBean.getNumber().equals(sortModel.getPhone())){
                                recordBean.setName("");
                                recordBean.setIsExite(false);
                            }
                        }
                        String recordjsonList = JSON.toJSONString(recordList);
                        mCache.put("record", recordjsonList);
                        //通知更新
                        Intent i = new Intent();
                        i.setAction("change_record");
                        getActivity().sendBroadcast(i);
                        break;
                }
                return false;
            }
        });
    }


    private float getRawSize(int unit, float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private List<SortModel> filledData(List<Contact> contact) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < contact.size(); i++) {
            SortModel sortModel = new SortModel();
            if (TextUtils.isEmpty(contact.get(i).getName())) {
                sortModel.setName("未知联系人");
            } else {
                sortModel.setName(contact.get(i).getName());
            }
            if (TextUtils.isEmpty(contact.get(i).getNumber())) {
                sortModel.setPhone("#");
            } else {
                sortModel.setPhone(contact.get(i).getNumber());
            }
            if (TextUtils.isEmpty(contact.get(i).getId())) {
                sortModel.setId("1111111");
            } else {
                sortModel.setId(contact.get(i).getId());
            }
            if (TextUtils.isEmpty(contact.get(i).getNote())) {
                sortModel.setNote("");
            } else {
                sortModel.setNote(contact.get(i).getNote());
            }

            String pinyin = characterParser.getSelling(contact.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }


    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(sortModel.getPhone())) {
                    continue;
                }
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString()) || sortModel.getPhone().contains(filterStr)) {
                    filterDateList.add(sortModel);
                }
            }
            Collections.sort(filterDateList, pinyinComparator);
        }
        adapter.updateListView(filterDateList);
    }

    List<Contact> contactList = new ArrayList<Contact>();

    private void getPhoneContacts() {
        //先加载缓存
        ACache mCache = ACache.get(getActivity());
        String contact = mCache.getAsString("contact");
        if(TextUtils.isEmpty(contact)){
            mCache.put("contact","[]");
            contact="[]";
        }
        if (!contact.equals("[]")) {
            contactList = JSON.parseArray(contact, Contact.class);
            not_data.setVisibility(View.GONE);
        } else {
            not_data.setVisibility(View.VISIBLE);
//            showPermissonDialog();
//            ContactsManager cm = new ContactsManager(getActivity(), getActivity().getContentResolver());
//            String dataList = cm.searchContact();
//            contactList = JSON.parseArray(dataList, Contact.class);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("friendfragment");
        loadData();
        initView();
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("friendfragment");
    }
    boolean isExchege = true;

    private void showWarnDialog() {

        final MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("将要与手机内通讯录互相同步 , 是否继续 ?")
                .setTitle("系统提示")
                .setNegativeButton("暂不同步", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        dialog.setPositiveButton("立即同步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CallRecordUtils callRecord = new CallRecordUtils();
//                callRecord.getDataList(getActivity());
                //手机数据库联系人dialog
                MyTask mTask = new MyTask();
                mTask.execute();
                loading.setVisibility(View.VISIBLE);
                tv_desc.setText("正在同步通讯录...");
                syn.setEnabled(false);
//
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
        getActivity().unregisterReceiver(receiver);
    }

    //监听联系人数据的监听对象
    private static ContentObserver mObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // 改变

        }
    };

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            showPermissonDialog();
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
                startActivity(intent);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
//            ContactsManager cm = new ContactsManager(getActivity(), getActivity().getContentResolver());
//            String dataList = cm.searchContact();
//            contactList = JSON.parseArray(dataList, Contact.class);
            showWarnDialog();
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
            contactList.clear();
            ContactsManager cm = new ContactsManager(getActivity(), getActivity().getContentResolver());
            String phoneContact = cm.searchContact();
            List<Contact> phoneContactList = JSON.parseArray(phoneContact, Contact.class);
            //本地联系人
            ACache mCache = ACache.get(getActivity());
            String locationContact = mCache.getAsString("contact");
            List<Contact> locationContactList = JSON.parseArray(locationContact, Contact.class);
            contactList.addAll(locationContactList);
            for (Contact contact : contactList) {
                isExchege = true;
                for (Contact contact1 : phoneContactList) {
                    if (contact.getNumber().equals(contact1.getNumber())) {
                        isExchege = false;
                        //有相同的就看是否需要更新
                        if (!contact.getName().equals(contact1.getName())) {
                            //名字相同就过
                            //名字不同就更新
                            ContactsManager manager = new ContactsManager(getActivity(), getActivity().getContentResolver());
                            String id = manager.getContactID(contact1.getName());
                            manager.updateContact(id, contact);
                        }
                    }
                }
                if (isExchege) {
                    //添加到数据库
                    ContactsManager manager = new ContactsManager(getActivity(), getActivity().getContentResolver());
                    String id = manager.addContact(contact);
                }
            }

            for (Contact contact : phoneContactList) {
                isExchege = true;

                if (TextUtils.isEmpty(contact.getName())) {
                    break;
                }

                for (Contact contact1 : contactList) {
                    if (contact.getNumber().equals(contact1.getNumber())) {
                        isExchege = false;
                        //有相同的就看是否需要更新
                        if (!contact.getName().equals(contact1.getName())) {

                        }
                    }
                }
                if (isExchege) {
                    //添加到本地
                    contactList.add(contact);
                }
            }

            String jsonList = JSON.toJSONString(contactList);
            mCache.put("contact", jsonList);
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
            syn.setEnabled(true);
            loading.setVisibility(View.GONE);
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
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

    private void saveReacord(String name, String phone,String note,String id) {
        ACache mCache = ACache.get(getActivity());
        String recordjson = mCache.getAsString("record");
        List<RecordBean> recordList = JSON.parseArray(recordjson, RecordBean.class);

        RecordBean recor = new RecordBean();
        recor.setName(name);
        recor.setNumber(phone);
        long time = System.currentTimeMillis();
        recor.setDate("");
        recor.setDatetime(time);
        recor.setIsExite(true);
        recor.setNote(note);
        recor.setId(id);
        recordList.add(0, recor);
        String recordjsonList = JSON.toJSONString(recordList);
        mCache.put("record", recordjsonList);
    }

    private boolean checkLoginStatue(){

        mLoginPreference = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        mIsLogin = mLoginPreference.getBoolean(KEY_IS_LOGIN, false);

        return mIsLogin;
    }
}
