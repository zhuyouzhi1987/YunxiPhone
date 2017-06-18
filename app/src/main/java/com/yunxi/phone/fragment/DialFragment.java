package com.yunxi.phone.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yunxi.phone.R;
import com.yunxi.phone.activity.CertificationActivity;
import com.yunxi.phone.activity.DailActivity;
import com.yunxi.phone.activity.DailDescActivity;
import com.yunxi.phone.activity.LoginActivity;
import com.yunxi.phone.bean.CallBean;
import com.yunxi.phone.bean.Contact;
import com.yunxi.phone.bean.RecordBean;
import com.yunxi.phone.eventtype.ChangeTabType;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.RefershType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.ContactsManager;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.PeriscopeLayout;
import com.yunxi.phone.utils.SingleButtonAlertDialog;
import com.yunxi.phone.utils.UserInfoUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

import static android.content.Context.VIBRATOR_SERVICE;

public class DialFragment extends Fragment implements OnClickListener {

    private TextView number1, number2, number3, number4, number5, number6, number7, number8, number9, number0, number_star, number_jing;
    private ImageView number_delete;

    private ImageView friend_btn;
    private AutoRelativeLayout dail_send_btn;

    private EditText show_number;

    private SharedPreferences mLoginPreference;


    private boolean mIsLogin;
    private boolean isIsExite = false;

    private TextView title, get_more;
    String id = "0";

    private ACache mCache;

    private String uid;

    private AutoRelativeLayout dail_desc;


    private String talkTime;

    private String bili;

    private String name = "";

    private String phone;

    private TextView hint;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;
    AutoLinearLayout loading;
    AutoRelativeLayout not_data;

    private PeriscopeLayout periscopeLayout;

    private AutoRelativeLayout egg_layout;

    private int count=0;

    private Timer mTimer=null;
    private TimerTask mTimerTask=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dial_fragment, container, false);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        init(view);

    }

    private void init(View v) {

        mSharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        periscopeLayout = (PeriscopeLayout) v.findViewById(R.id.periscope);
        egg_layout=(AutoRelativeLayout)v.findViewById(R.id.egg_layout);
        egg_layout.setOnClickListener(null);

        mCache = ACache.get(getActivity());

        uid = mCache.getAsString("user_id");

        phone = mCache.getAsString("phone");

        EventBus.getDefault().register(this);


        dail_desc = (AutoRelativeLayout) v.findViewById(R.id.dail_desc);
        dail_desc.setOnClickListener(this);

        hint = (TextView) v.findViewById(R.id.hint);


        number1 = (TextView) v.findViewById(R.id.number1);
        number2 = (TextView) v.findViewById(R.id.number2);
        number3 = (TextView) v.findViewById(R.id.number3);
        number4 = (TextView) v.findViewById(R.id.number4);
        number5 = (TextView) v.findViewById(R.id.number5);
        number6 = (TextView) v.findViewById(R.id.number6);
        number7 = (TextView) v.findViewById(R.id.number7);
        number8 = (TextView) v.findViewById(R.id.number8);
        number9 = (TextView) v.findViewById(R.id.number9);
        number0 = (TextView) v.findViewById(R.id.number0);
        number_star = (TextView) v.findViewById(R.id.number_star);
        number_jing = (TextView) v.findViewById(R.id.number_jing);
        dail_send_btn = (AutoRelativeLayout) v.findViewById(R.id.dail_send_btn);
        show_number = (EditText) v.findViewById(R.id.show_number);
        number_delete = (ImageView) v.findViewById(R.id.delete_number_btn);
        number_delete.setOnClickListener(this);
        number_delete.setVisibility(View.GONE);

        friend_btn = (ImageView) v.findViewById(R.id.friend_btn);
        friend_btn.setOnClickListener(this);
        friend_btn.setVisibility(View.VISIBLE);


        title = (TextView) v.findViewById(R.id.title_desc);
        get_more = (TextView) v.findViewById(R.id.get_more);


        loading = (AutoLinearLayout) v.findViewById(R.id.loading);

        not_data = (AutoRelativeLayout) v.findViewById(R.id.not_data);
        AutoRelativeLayout get_data = (AutoRelativeLayout) v.findViewById(R.id.get_data);


        get_more.setOnClickListener(this);
        get_data.setOnClickListener(this);

        number1.setOnClickListener(this);
        number2.setOnClickListener(this);
        number3.setOnClickListener(this);
        number4.setOnClickListener(this);
        number5.setOnClickListener(this);
        number6.setOnClickListener(this);
        number7.setOnClickListener(this);
        number8.setOnClickListener(this);
        number9.setOnClickListener(this);
        number0.setOnClickListener(this);
        number_star.setOnClickListener(this);
        number_jing.setOnClickListener(this);
        dail_send_btn.setOnClickListener(this);


        number_delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate();

                show_number.setText("");

                return false;
            }
        });


        show_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s == null || s.length() == 0) {

                    number_delete.setVisibility(View.GONE);
                    friend_btn.setVisibility(View.VISIBLE);

                    hint.setVisibility(View.VISIBLE);

                    return;
                } else {
                    number_delete.setVisibility(View.VISIBLE);
                    friend_btn.setVisibility(View.GONE);

                    hint.setVisibility(View.GONE);
                }


                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    show_number.setText(sb.toString());
                    show_number.setSelection(index);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(getString(R.string.egg).equals(editable.toString())){

                    startEgg();

                }

            }
        });


        if (checkLoginStatue()) {
            loadData();
        } else {
            title.setText("新注册用户立送200云朵");
            get_more.setText("注册领取");
        }


    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator)getActivity().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }


    private boolean checkLoginStatue() {

        mLoginPreference = getActivity().getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        mIsLogin = mLoginPreference.getBoolean(KEY_IS_LOGIN, false);

        return mIsLogin;
    }


    private void loadData() {

        showLoading();
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

                hideLoading();
                CallBean bean = JSON.parseObject(result,
                        CallBean.class);
                not_data.setVisibility(View.GONE);

                if ("200".equals(bean.getResult())) {

                    talkTime = bean.getData().getTalkminutes();
                    bili = bean.getData().getProportion();

                    if ("".equals(talkTime)) {

                        title.setText("完成身份认证即可拨打电话");
                        get_more.setText("立即认证");

                    } else {

                        title.setText("可用通话" + talkTime + "分钟 ( " + bean.getData().getProportion() + "云朵=1分钟 )");
                        get_more.setText("获取更多");

                    }


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
                hideLoading();
                L.d("异常" + ex.toString());
                not_data.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();

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


    private void change(String number) {
        StringBuffer sb = new StringBuffer(show_number.getText());
        show_number.setText(sb.append(number));
    }


    /**
     * 点击删除按钮删除操作
     */
    private void delete() {
        if (show_number.getText() != null && show_number.getText().length() > 1) {
            StringBuffer sb = new StringBuffer(show_number.getText());
            show_number.setText(sb.substring(0, sb.length() - 1).trim());
        } else if (show_number.getText() != null && !"".equals(show_number.getText())) {
            show_number.setText("");
        }
    }



    private void startEgg(){

        egg_layout.setVisibility(View.VISIBLE);

        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {

                @Override
                public void run() {
                    // 需要做的事:发送消息
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };

        }

        mTimer.schedule(mTimerTask, 0,200);



    }


    private void stopEgg(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        count=0;
        egg_layout.setVisibility(View.GONE);
    }



    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                count++;

                if(count<100){
                    periscopeLayout.addHeart();
                }else{

                    stopEgg();
                }



            }
            super.handleMessage(msg);
        };
    };


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.dail_desc:

                Intent dail_desc = new Intent(getActivity(), DailDescActivity.class);
                startActivity(dail_desc);

                break;


            case R.id.number0:

                change("0");

                break;


            case R.id.number1:

                change("1");

                break;

            case R.id.number2:

                change("2");

                break;

            case R.id.number3:

                change("3");

                break;

            case R.id.number4:

                change("4");

                break;
            case R.id.number5:

                change("5");

                break;
            case R.id.number6:

                change("6");

                break;
            case R.id.number7:


                change("7");

                break;
            case R.id.number8:

                change("8");

                break;
            case R.id.number9:

                change("9");

                break;
            case R.id.number_star:

                change("*");

                break;
            case R.id.number_jing:

                change("#");

                break;
            case R.id.delete_number_btn:

                delete();

                break;

            case R.id.dail_send_btn:


                if (checkLoginStatue()) {

                    phone = mCache.getAsString("phone");


                    if (phone.startsWith("14") || phone.startsWith("17")) {

                        showWarnDialog();


                    } else {

                        if ("".equals(talkTime)) {

                            //去认证
                            Intent intent = new Intent(getActivity(), CertificationActivity.class);
                            startActivity(intent);
                        } else {


                            if ("0".equals(talkTime)) {

                                showNotCloudDialog(bili);

                            } else {


                                if (!"".equals(show_number.getText().toString())) {


                                    if (UserInfoUtil.isMobileNO(show_number.getText().toString().replace(" ", ""))) {

                                        if (phone.equals(show_number.getText().toString().replace(" ", ""))) {

                                            Toast.makeText(getActivity(), "不支持与本机号码进行通话", Toast.LENGTH_SHORT).show();

                                        } else {
                                            if (!show_number.getText().toString().startsWith("1")) {
                                                showSingleDialog();
                                            } else {
                                                if (show_number.getText().toString().startsWith("14") || show_number.getText().toString().startsWith("17")) {
                                                    showSingleDialog();
                                                } else {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            String temp = show_number.getText().toString().replace(" ", "");


                                                            ACache mCache = ACache.get(getActivity());
                                                            String contact = mCache.getAsString("contact");
                                                            List<Contact> cacheList = JSON.parseArray(contact, Contact.class);
                                                            for (Contact contactBean : cacheList)
                                                                if (contactBean.getNumber().equals(temp)) {
                                                                    name = contactBean.getName();

                                                                    id = contactBean.getId();
                                                                    break;
                                                                }


                                                            Intent i = new Intent(getActivity(), DailActivity.class);
                                                            i.putExtra("phone", "+86" + temp);
                                                            i.putExtra("name", name);
                                                            i.putExtra("formatPhone", show_number.getText().toString());
                                                            i.putExtra("talk", talkTime);

                                                            startActivity(i);
                                                            if (!TextUtils.isEmpty(name)) {
                                                                isIsExite = true;
                                                            }
                                                            saveReacord(name, temp, isIsExite, id);
                                                        }
                                                    }).start();

                                                }
                                            }
                                        }

                                    } else {

                                        Toast.makeText(getActivity(), "电话格式错误!", Toast.LENGTH_SHORT).show();

                                    }


                                } else {

                                    Toast.makeText(getActivity(), "请输入电话号码!", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }

                    }

                } else {

                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from", "app_in");
                    startActivity(login);
                }

                break;

            case R.id.get_more:

                if (checkLoginStatue()) {

                    if ("".equals(talkTime)) {

                        //去认证
                        Intent intent = new Intent(getActivity(), CertificationActivity.class);
                        startActivity(intent);

                    } else {
                        //获取更多
                        EventBus.getDefault().post(
                                new ChangeTabType("cloud"));

                    }

                } else {

                    //去登录
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("from", "app_in");
                    startActivity(login);

                }

                break;

            case R.id.friend_btn:
                EventBus.getDefault().post(
                        new ChangeTabType("friend"));
                break;

            case R.id.get_data:
                loadData();
                break;
        }


    }

    private void showWarnDialog() {
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


    private void showNotCloudDialog(String bili) {
        MyAlertDialog dialog = new MyAlertDialog(getActivity())
                .builder().setMsg("您账户内暂无可用通话时长 , 请完成任务获取云朵 , " + bili + "云朵=1分钟 ！")
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


    public void onEventMainThread(RefershType event) {
        //登录之后 回来刷新数据
        loadData();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void saveReacord(String name, String phone, boolean isIsExite, String id) {
        ACache mCache = ACache.get(getActivity());
        String recordjson = mCache.getAsString("record");
        List<RecordBean> recordList = JSON.parseArray(recordjson, RecordBean.class);
        RecordBean recor = new RecordBean();
        recor.setName(name);
        recor.setNumber(phone);
        long time = System.currentTimeMillis();
        recor.setDate("");
        recor.setDatetime(time);
        recor.setIsExite(isIsExite);
        String contactID = new ContactsManager(getActivity(), getActivity().getContentResolver()).getContactID(name);
        if (!contactID.equals("0")) {
            //存在
            String note = new ContactsManager(getActivity(), getActivity().getContentResolver()).getNote(name);
            recor.setNote(note);
            recor.setId(contactID);
        } else {
            recor.setId(id);
        }

        recordList.add(0, recor);
        String recordjsonList = JSON.toJSONString(recordList);
        mCache.put("record", recordjsonList);
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

    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}
