package com.yunxi.phone.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.StatusBarUtil;
import com.yunxi.phone.utils.UserInfoUtil;
import com.yunxi.phone.utils.VsMd5;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.common.Callback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.agora.AgoraAPI;



/**
 * Created by bond on 16/2/19.
 */
public class DailActivity extends BaseActivity implements View.OnClickListener{


    //测试时使用

    private AgoraAPI m_agoraAPI;

    public static final String signKey = "2aba37f79db0449eb05cf0f61182f7ad";
    public static final String vendorkey = "4c7f9e4efe004e69be248eb03e288938";

    private String room;


    private ACache mCache;

    private String uid;

    private String phone;
    private String formatPhone;
    private String name;
    private String talk;


    private AutoLinearLayout mianti_layout;
    private AutoLinearLayout nosound_layout;

    private ImageView mianti_btn,nosound_btn,shutdown_btn;

    private Chronometer timer;

    private boolean isOpenMianti=false;
    private boolean isOpenNosound=false;

    private TextView phoneNumber;
    private TextView dail_statue;

    private String own_phone;

    private String call_id;

    private boolean isCalled=false;


    // wifi相关
    IntentFilter wifiIntentFilter;  // wifi监听器

    private ImageView wifi_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBatInfoReceiver, filter);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_dail;
    }

    @Override
    protected void findById() {

        wifi_image=$(R.id.wifi_icon);

        shutdown_btn=$(R.id.shutdown);

        mianti_btn=$(R.id.mianti_btn);
        mianti_layout=$(R.id.mianti_layout);
        mianti_layout.setOnClickListener(this);

        nosound_btn=$(R.id.nosound_btn);
        nosound_layout=$(R.id.nosound_layout);
        nosound_layout.setOnClickListener(this);

        shutdown_btn=$(R.id.shutdown);
        shutdown_btn.setOnClickListener(this);

        timer=$(R.id.timer);

        phoneNumber=$(R.id.phone_number);
        dail_statue=$(R.id.dail_statue);


    }

    @Override
    protected void regListener() {
        shutdown_btn.setOnClickListener(this);

    }


    // 声明wifi消息处理过程
    private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifi_state = intent.getIntExtra("wifi_state", 0);
            int level = Math.abs(((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo().getRssi());
            L.d("wifi信号级别" + level);
            switch (wifi_state) {
                case WifiManager.WIFI_STATE_DISABLING:
                    L.d("WIFI网卡正在关闭");
                    wifi_image.setImageResource(R.drawable.wifi_sel);
                    wifi_image.setImageLevel(level);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    L.d("WIFI网卡不可用");
                    wifi_image.setImageResource(R.drawable.wifi_sel);
                    wifi_image.setImageLevel(level);
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    wifi_image.setImageResource(R.drawable.wifi_sel);
                    wifi_image.setImageLevel(level);
                    L.d("WIFI网卡正在打开");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    L.d("WIFI网卡可用");
                    wifi_image.setImageResource(R.drawable.wifi_sel);
                    wifi_image.setImageLevel(level);
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    L.d("WIFI网卡状态不可知");
                    wifi_image.setImageResource(R.drawable.wifi_sel);
                    wifi_image.setImageLevel(level);
                    break;
            }
        }
    };

    @Override
    protected void init() {

        // wifi
        wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);


        //开启通知栏
        openNotification();
        phone=getIntent().getStringExtra("phone");
        formatPhone=getIntent().getStringExtra("formatPhone");
        talk=getIntent().getStringExtra("talk");
        name=getIntent().getStringExtra("name");


        if("".equals(name) || "未知联系人".equals(name)){

            phoneNumber.setText(formatPhone);

        }else{

            phoneNumber.setText(name);

        }



        mCache = ACache.get(DailActivity.this);

        uid=mCache.getAsString("user_id");
        own_phone=mCache.getAsString("phone");



        // 初始化
        m_agoraAPI = AgoraAPI.getInstanceWithKey(DailActivity.this, vendorkey);
        m_agoraAPI.getMedia().enableAudioVolumeIndication(1000, 3);
        m_agoraAPI.getMedia().setEnableSpeakerphone(false);



        m_agoraAPI.callbackSet(new AgoraAPI.CallBack() {
            // 登录成功
            @Override
            public void onLoginSuccess(int uid, int fd) {
                L.d("打电话功能 登录成功");

                m_agoraAPI.channelJoin(room);

            }

            // 登录失败
            @Override
            public void onLoginFailed(int ecode) {
                L.d("打电话功能 登录失败 " + ecode);

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {

                        mHandler.sendEmptyMessage(3);

                    }


                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);


            }

            // 登出
            @Override
            public void onLogout(int ecode) {
                L.d("打电话功能 退出登录" + ecode);

                DailActivity.this.finish();
            }

            // SDK日志
            @Override
            public void onLog(String txt) {

            }

            // 加入频道
            @Override
            public void onChannelJoined(String channleID) {
                L.d("加入房间channel成功 " + channleID);

                m_agoraAPI.getMedia().setEnableSpeakerphone(false);

                mHandler.sendEmptyMessage(4);


                int second=Integer.parseInt(talk)*60;

                Map<String,String> map=new HashMap<String, String>();
                map.put("sip_header:X-extra",uid+","+second);



                String jsonString= JSON.toJSONString(map);

                L.d("提交的json串==="+jsonString);

                m_agoraAPI.channelInvitePhone3(room, phone, "+86"+own_phone, jsonString);




            }

            //离开频道
            @Override
            public void onChannelLeaved(String channleID, int ecode) {
                L.d("离开房间 " );


                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        m_agoraAPI.logout();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);

            }

            @Override
            public void onInviteReceived(String channleID, String account,
                                         int uid) {

                L.d("对方收到呼叫邀请");

                L.d("Received Invitation from " + account + ":" + uid
                        + " to join " + channleID);
                m_agoraAPI.channelInviteAccept(channleID, account, uid);

                m_agoraAPI.channelJoin(channleID);
            }

            @Override
            public void onChannelUserJoined(String account, int uid) {
                L.d("加入成功后，又有新人加入："+account + ":" + uid );
            }

            //加入房间失败
            @Override
            public void onChannelJoinFailed(String chanID, int ecode) {
                L.d("加入房间失败:"+chanID+" ecode:"+ecode);

                mHandler.sendEmptyMessage(3);

                m_agoraAPI.logout();

            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                L.d("加入成功后，有人离开："+account + ":" + uid);
            }

            @Override
            public void onChannelUserList(String[] accounts, int[] uids) {
                L.d("房间人列表:");
                for (int i = 0; i < accounts.length; i++) {
                    L.d(accounts[i] + ":" + uids[i]);
                }
            }

            //对方响铃
            @Override
            public void onInviteReceivedByPeer(String channleID,
                                               String account, int uid) {
                L.d("对方收到并响铃 " + account + ":" + uid);
            }

            //对方挂断
            @Override
            public void onInviteEndByPeer(String channelID, String account,
                                          int uid) {
                L.d("对方挂断电话 " + account + ":" + uid);

                mHandler.sendEmptyMessage(2);

                m_agoraAPI.channelLeave(channelID);

            }

            //自己 挂断
            @Override
            public void onInviteEndByMyself(String channelID,
                                            String account, int uid) {
                L.d("自己挂断电话 " + account + ":" + uid);

                mHandler.sendEmptyMessage(2);

                m_agoraAPI.channelLeave(channelID);

            }

            //对方接听
            @Override
            public void onInviteAcceptedByPeer(String channleID,
                                               String account, int uid) {
                L.d("对方接听了电话 " + account + ":" + uid);

                isCalled=true;

                mHandler.sendEmptyMessage(1);


            }

            //呼叫失败
            @Override
            public void onInviteFailed(String channelID, String account, int uid, int ecode) {
                L.d("呼叫失败 " + account + ":" + uid);

                mHandler.sendEmptyMessage(3);

                m_agoraAPI.channelLeave(channelID);


            }

            @Override
            public void onMessageChannelReceive(String channelID,
                                                String account, int uid, String msg) {
                L.d("recv channel msg " + channelID + " " + account + " : "
                        + msg);
            }

            //对方拒接
            @Override
            public void onInviteRefusedByPeer(String channelID, String account, int uid) {
                L.d("对方拒接电话");

                mHandler.sendEmptyMessage(5);

                m_agoraAPI.channelLeave(channelID);

            }
        });




        room= UUID.randomUUID().toString();

        String token = gettoken();

        m_agoraAPI.login(vendorkey, uid, token, 0, "");



    }
    private NotificationCompat.Builder builder;
    private NotificationManager nm;
    private void openNotification() {
        builder = new NotificationCompat.Builder(this);
        builder.setPriority(Notification.PRIORITY_MIN);
        Intent in = new Intent(this, DailActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        in.putExtra("notice", true);
//        in.putExtra("step",content);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, in, 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.app_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_logo));
        builder.setTicker("语音");
        builder.setContentTitle("语音");
        builder.setOngoing(true);
        builder.setContentText("正在通话中...");
        Notification notification = builder.build();
//       startForeground(0, notification);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(R.string.app_name, notification);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1:

                    dail_statue.setVisibility(View.GONE);
                    timer.setVisibility(View.VISIBLE);

                    timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                    timer.start();


                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {

                            m_agoraAPI.channelInviteEnd(room,phone,0);

                        }


                    };
                    Timer  t= new Timer();
                    t.schedule(task, Integer.parseInt(talk)*60*1000);

                    call_id=UserInfoUtil.getTimeStamp()+room;

                    submitCall(call_id,phone,own_phone,"1");


                    break;

                case 2:
                    nm.cancel(R.string.app_name);
                    shutdown_btn.setImageResource(R.mipmap.shutdown_press);

                    timer.stop();
                    timer.setVisibility(View.GONE);

                    dail_statue.setVisibility(View.VISIBLE);
                    dail_statue.setText("结束通话");

                    if(isCalled){

                        submitCall(call_id,phone,own_phone,"2");

                    }

                    break;

                case 3://呼叫失败

                    shutdown_btn.setImageResource(R.mipmap.shutdown_press);

                    dail_statue.setVisibility(View.VISIBLE);
                    dail_statue.setText("呼叫失败");
                    DailActivity.this.finish();
                    nm.cancel(R.string.app_name);
                    break;

                case 4://呼叫中

//                    m_agoraAPI.getMedia().setEnableSpeakerphone(false);

                    dail_statue.setVisibility(View.VISIBLE);
                    dail_statue.setText("呼叫中");

                    break;

                case 5://对方拒接

                    shutdown_btn.setImageResource(R.mipmap.shutdown_press);

                    dail_statue.setVisibility(View.VISIBLE);
                    dail_statue.setText("结束通话");
                    nm.cancel(R.string.app_name);
                    break;



                default:
                    break;
            }
        }
    };


    private void submitCall(String call_id, String otherPhone, String myPhone, String type) {

        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(DailActivity.this);

        map.put("user_id",mCache.getAsString("user_id"));
        map.put("userkey",mCache.getAsString("token"));
        map.put("call_id",call_id);
        map.put("calling_number",otherPhone);
        map.put("called_number",myPhone);
        map.put("type",type);

        XUtil.Post(DailActivity.this, AddressApi.SUBMIT_CALL, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                L.d("提交的通话==="+result.toString());

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                L.d("提交的通话失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });


    }


    public static String getTime(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }


    // 获取token
    protected String gettoken() {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_future = sdf.format(c.getTime());
        String list = VsMd5.md5(uid + vendorkey + signKey + getTime(time_future));
        String token = "1:" + vendorkey + ":" + getTime(time_future) + ":" + list;
        return token;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mBatInfoReceiver != null) {
            try {
                unregisterReceiver(mBatInfoReceiver);
            }
            catch(Exception e) {
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        registerReceiver(wifiIntentReceiver, wifiIntentFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        unregisterReceiver(wifiIntentReceiver);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.shutdown:

                shutdown_btn.setImageResource(R.mipmap.shutdown_press);

                m_agoraAPI.channelInviteEnd(room,phone,0);

                break;


            case R.id.mianti_layout:

                if(isOpenMianti){

                    L.d("免提关闭了");

                    isOpenMianti=false;
                    mianti_btn.setImageResource(R.mipmap.mianti_close);
                    m_agoraAPI.getMedia().setEnableSpeakerphone(false);


                }else{

                    L.d("免提开启了");

                    isOpenMianti=true;

                    mianti_btn.setImageResource(R.mipmap.mianti_open);

                    m_agoraAPI.getMedia().setEnableSpeakerphone(true);

                }


                break;


            case R.id.nosound_layout:

                if(isOpenNosound){

                    L.d("静音关闭了");

                    isOpenNosound=false;

                    nosound_btn.setImageResource(R.mipmap.nosound_close);
                    m_agoraAPI.getMedia().muteLocalAudioStream(false);


                }else{

                    L.d("静音开启了");

                    isOpenNosound=true;

                    nosound_btn.setImageResource(R.mipmap.nosound_open);
                    m_agoraAPI.getMedia().muteLocalAudioStream(true);


                }



                break;


        }

    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if(Intent.ACTION_SCREEN_OFF.equals(action)) {

                m_agoraAPI.channelInviteEnd(room,phone,0);

            }
        }
    };




    @Override
    protected void setStatusBar() {

        StatusBarUtil.setTransparent(DailActivity.this);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
