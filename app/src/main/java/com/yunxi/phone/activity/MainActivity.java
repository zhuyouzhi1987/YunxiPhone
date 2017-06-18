package com.yunxi.phone.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.base.AppManager;
import com.yunxi.phone.eventtype.ChangeTabType;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.RefershType;
import com.yunxi.phone.fragment.CloudFragment;
import com.yunxi.phone.fragment.DialFragment;
import com.yunxi.phone.fragment.FriendFragment;
import com.yunxi.phone.fragment.RecordFragment;
import com.yunxi.phone.service.StepData;
import com.yunxi.phone.service.StepDcretor;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAutoUpdate;
import com.yunxi.phone.utils.SPUtils;
import com.yunxi.phone.utils.StatusBarUtil;
import com.yunxi.phone.utils.UpdateCallBack;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity implements View.OnClickListener,Handler.Callback {


    private static final Handler sHandler = new Handler();
    private static final int FINISH_CONFIRM_INTERVAL = 1500;
    private boolean mBackPressed = false;


    private AutoLinearLayout mRecord_layout;
    private AutoLinearLayout mFriend_layout;
    private AutoLinearLayout mDial_layout;
    private AutoLinearLayout mCloud_layout;

    private ImageView mTabRecord_img;
    private ImageView mTabFriend_img;
    private ImageView mTabDial_img;
    private ImageView mTabCloud_img;

    private TextView mTabRecord_txt;
    private TextView mTabFriend_txt;
    private TextView mTabDial_txt;
    private TextView mTabCloud_txt;
    private Fragment mTab01,mTab02,mTab03,mTab04,currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.getInstance().addFragmentActivity(this);

        setContentView(R.layout.activity_main);

        StatusBarUtil.setColor(this, getResources().getColor(R.color.appMainColor));

        EventBus.getDefault().register(this);
        initUpdate();
        initView();
        initTab();

    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initView(){

        mRecord_layout=(AutoLinearLayout)findViewById(R.id.tab_record);
        mFriend_layout=(AutoLinearLayout)findViewById(R.id.tab_friend);
        mDial_layout=(AutoLinearLayout)findViewById(R.id.tab_dial);
        mCloud_layout=(AutoLinearLayout)findViewById(R.id.tab_cloud);

        mTabRecord_img=(ImageView)findViewById(R.id.tab_record_img);
        mTabFriend_img=(ImageView)findViewById(R.id.tab_friend_img);
        mTabDial_img=(ImageView)findViewById(R.id.tab_dial_img);
        mTabCloud_img=(ImageView)findViewById(R.id.tab_cloud_img);

        mTabRecord_txt=(TextView)findViewById(R.id.tab_record_txt);
        mTabFriend_txt=(TextView)findViewById(R.id.tab_friend_txt);
        mTabDial_txt=(TextView)findViewById(R.id.tab_dial_txt);
        mTabCloud_txt=(TextView)findViewById(R.id.tab_cloud_txt);

        mRecord_layout.setOnClickListener(this);
        mFriend_layout.setOnClickListener(this);
        mDial_layout.setOnClickListener(this);
        mCloud_layout.setOnClickListener(this);


    }

    /**
     * 计步相关的数据
     */
    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL = 500;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler());
    private Handler delayHandler;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, AddressApi.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case AddressApi.MSG_FROM_SERVER:
                // 更新界面上的步数
                delayHandler.sendEmptyMessageDelayed(AddressApi.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case AddressApi.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, AddressApi.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
        }
        return false;
    }
    /**
     * 初始化底部标签
     */
    private void initTab() {
        if (mTab03 == null) {
            mTab03 = new DialFragment();
        }

        if (!mTab03.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction().add(R.id.id_content, mTab03).commitAllowingStateLoss();

            // 记录当前Fragment
            currentFragment = mTab03;
            // 设置图片的变化

            mTabRecord_img.setImageResource(R.mipmap.tab_record_no);
            mTabFriend_img.setImageResource(R.mipmap.tab_friend_no);
            mTabDial_img.setImageResource(R.mipmap.tab_dial_hl);
            mTabCloud_img.setImageResource(R.mipmap.tab_cloud_no);

            mTabRecord_txt.setTextColor(Color.parseColor("#666666"));
            mTabFriend_txt.setTextColor(Color.parseColor("#666666"));
            mTabDial_txt.setTextColor(Color.parseColor("#00CBAE"));
            mTabCloud_txt.setTextColor(Color.parseColor("#666666"));


        }
    }

    /**
     * 点击第一个tab
     */
    private void clickTab1Layout() {
        if (mTab01 == null) {
            mTab01 = new RecordFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mTab01);

        // 设置底部tab变化

        mTabRecord_img.setImageResource(R.mipmap.tab_record_hl);
        mTabFriend_img.setImageResource(R.mipmap.tab_friend_no);
        mTabDial_img.setImageResource(R.mipmap.tab_dial_no);
        mTabCloud_img.setImageResource(R.mipmap.tab_cloud_no);

        mTabRecord_txt.setTextColor(Color.parseColor("#00CBAE"));
        mTabFriend_txt.setTextColor(Color.parseColor("#666666"));
        mTabDial_txt.setTextColor(Color.parseColor("#666666"));
        mTabCloud_txt.setTextColor(Color.parseColor("#666666"));
    }

    /**
     * 点击第二个tab
     */
    private void clickTab2Layout() {
        if (mTab02 == null) {
            mTab02 = new FriendFragment();

        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mTab02);

        // 设置底部tab变化


        mTabRecord_img.setImageResource(R.mipmap.tab_record_no);
        mTabFriend_img.setImageResource(R.mipmap.tab_friend_hl);
        mTabDial_img.setImageResource(R.mipmap.tab_dial_no);
        mTabCloud_img.setImageResource(R.mipmap.tab_cloud_no);

        mTabRecord_txt.setTextColor(Color.parseColor("#666666"));
        mTabFriend_txt.setTextColor(Color.parseColor("#00CBAE"));
        mTabDial_txt.setTextColor(Color.parseColor("#666666"));
        mTabCloud_txt.setTextColor(Color.parseColor("#666666"));
    }


    /**
     * 点击第三个tab
     */
    private void clickTab3Layout() {
        if (mTab03 == null) {
            mTab03 = new DialFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mTab03);

        // 设置底部tab变化
        mTabRecord_img.setImageResource(R.mipmap.tab_record_no);
        mTabFriend_img.setImageResource(R.mipmap.tab_friend_no);
        mTabDial_img.setImageResource(R.mipmap.tab_dial_hl);
        mTabCloud_img.setImageResource(R.mipmap.tab_cloud_no);

        mTabRecord_txt.setTextColor(Color.parseColor("#666666"));
        mTabFriend_txt.setTextColor(Color.parseColor("#666666"));
        mTabDial_txt.setTextColor(Color.parseColor("#00CBAE"));
        mTabCloud_txt.setTextColor(Color.parseColor("#666666"));
        EventBus.getDefault().post(
                new RefershType(""));
    }

    /**
     * 点击第四个tab
     */
    private void clickTab4Layout() {
        if (mTab04 == null) {
            mTab04 = new CloudFragment();
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), mTab04);

        // 设置底部tab变化
        mTabRecord_img.setImageResource(R.mipmap.tab_record_no);
        mTabFriend_img.setImageResource(R.mipmap.tab_friend_no);
        mTabDial_img.setImageResource(R.mipmap.tab_dial_no);
        mTabCloud_img.setImageResource(R.mipmap.tab_cloud_hl);

        mTabRecord_txt.setTextColor(Color.parseColor("#666666"));
        mTabFriend_txt.setTextColor(Color.parseColor("#666666"));
        mTabDial_txt.setTextColor(Color.parseColor("#666666"));
        mTabCloud_txt.setTextColor(Color.parseColor("#00CBAE"));
    }

    /**
     * 添加或者显示碎片
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(R.id.id_content, fragment).commitAllowingStateLoss();
        } else {
            transaction.hide(currentFragment).show(fragment).commitAllowingStateLoss();
        }

        currentFragment = fragment;
    }


    private final Runnable mBackFlagCleaner = new Runnable() {
        @Override
        public void run() {
            mBackPressed = false;
        }
    };

    public void handleBackPressed() {
        sHandler.removeCallbacks(mBackFlagCleaner);
        if (!mBackPressed) {
            Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
            sHandler.postDelayed(mBackFlagCleaner, FINISH_CONFIRM_INTERVAL);
            mBackPressed = true;
        } else {

            AppManager.getInstance().AppExit(MainActivity.this);
            finish();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {

                handleBackPressed();
                return true;
        }
        return super.dispatchKeyEvent(event);
    }


    public void onEventMainThread(LogoutCloseType event) {
        StepDcretor.CURRENT_SETP=0;
        try {
            StepData stepData = YunxiApplication.db.selector(StepData.class).where("today", "=", getTodayDate()).findFirst();
            if (stepData != null){
                StepDcretor.CURRENT_SETP=0;
                stepData.setToday(getTodayDate());
                stepData.setStep(0 + "");
                YunxiApplication.db.update(stepData);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        MainActivity.this.finish();

    }
    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public void onEventMainThread(ChangeTabType event){

        if("cloud".equals(event.getMsg())){
            clickTab4Layout();
        }else if("friend".equals(event.getMsg())){
            clickTab2Layout();
        }else if("lock".equals(event.getMsg())){
            clickTab3Layout();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tab_record:
                clickTab1Layout();

                break;
            case R.id.tab_friend:
                clickTab2Layout();
                break;
            case R.id.tab_dial:

                clickTab3Layout();
                break;

            case R.id.tab_cloud:
                clickTab4Layout();
                break;
            default:
                break;
        }
    }


    private void initUpdate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String format = df.format(new Date());// new Date()为获取当前系统时间
        if (!SPUtils.get(getApplicationContext(), "update", "0").equals(format)) {
            MyAutoUpdate update = new MyAutoUpdate(this);
            update.check(new UpdateCallBack() {
                @Override
                public void call(boolean flag) {

                }
            });
            SPUtils.put(getApplicationContext(), "update", format);
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
