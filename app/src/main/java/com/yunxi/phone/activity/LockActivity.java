package com.yunxi.phone.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.LockInfo;
import com.yunxi.phone.eventtype.ChangeTabType;
import com.yunxi.phone.utils.AnimatorUtils;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.SPUtils;
import com.yunxi.phone.utils.StatusBarUtil;
import com.yunxi.phone.utils.UserInfoUtil;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.ex.DbException;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/24.
 */
public class LockActivity extends BaseActivity implements View.OnClickListener {
    AutoRelativeLayout lock_view3;
    AutoRelativeLayout test;
    ImageView iv_phone;
    ImageView iv_hongbao;
    ImageView iv_lock;
    ImageView lock_bg;
    ImageView iv_on;
    int oldx = 0;
    int oldy = 0;
    int upX = 0;
    int upY = 0;

    public static LockActivity activity = null;

    private AnimationDrawable ad;

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;


    @Override
    protected int getLayoutId() {

        return R.layout.lock;
    }

    @Override
    protected void findById() {

        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);


        lock_view3 = $(R.id.lock_view3);
        iv_phone = $(R.id.iv_phone);
        iv_hongbao = $(R.id.iv_hongbao);
        iv_lock = $(R.id.iv_lock);
        test = $(R.id.test);
        lock_bg = $(R.id.lock_bg);
        iv_on = $(R.id.iv_on);
    }

    @Override
    protected void regListener() {
        iv_phone.setOnClickListener(this);
        iv_hongbao.setOnClickListener(this);
    }

    @Override
    protected void init() {
        int is_UseId = (int) SPUtils.get(this, "isUse", 0);
        //获取数据库保存图片

        iv_on.setBackgroundResource(R.drawable.unlock);
        ad = (AnimationDrawable) iv_on.getBackground();
        //播放动画
        ad.start();


        try {
            //获取到
            LockInfo lockInfo = YunxiApplication.db.selector(LockInfo.class).where("isUse", "=", "1").findFirst();
            if (null != lockInfo) {
                //有内容展示
            } else {

                //没内容展示默认
            }
        } catch (DbException e) {
            e.printStackTrace();
        }


        if (is_UseId == 0) {
            //默认图片
        } else {
            boolean exits = isExits(is_UseId);
            if (exits) {
                //存在
                Glide.with(this).load(Environment
                        .getExternalStorageDirectory() + "/YX/lock/background" + is_UseId + ".jpg").into(lock_bg);
                Glide.with(this).load(Environment
                        .getExternalStorageDirectory() + "/YX/lock/phone" + is_UseId + ".jpg").into(iv_phone);
                Glide.with(this).load(Environment
                        .getExternalStorageDirectory() + "/YX/lock/red" + is_UseId + ".jpg").into(iv_hongbao);
                Glide.with(this).load(Environment
                        .getExternalStorageDirectory() + "/YX/lock/unlock" + is_UseId + ".jpg").into(iv_lock);
            } else {
                //默认图片
                SPUtils.put(getApplicationContext(), "isUse", 0);
            }

        }


        activity = this;
//        lock_view3.setOnDownListener(new LockView3.OnDownListener() {
//            @Override
//            public void onDown(float x, float y) {
//                oldx = (int) x;
//                oldy = (int) y;
//            }
//
//            @Override
//            public void onUp(float x, float y) {
//                if (menu_layout.isShown()) {
//                    return;
//                }
//                if (x > view.getLeft() && x < AppUtils.getWindowWidth(LockScreenActivity.this) && y < time_weather.getBottom() && y > time_weather.getTop()) {
//                    return;
//                }
//                if (MApplication.getInstance().menu_fast) {
//                    if (x > AppUtils.getWindowWidth(LockScreenActivity.this) / 2) {
//                        L.d("右侧菜单显示或隐藏");
//                        //上报
//                        saveReport(Consts.PAGE_LOCKSCREN, Consts.EVENT_RIGHTMENU_CLICK);
//                        menuChangeRight();
//                    } else {
//                        //上报
//                        saveReport(Consts.PAGE_LOCKSCREN, Consts.EVENT_LEFTMENU_CLICK);
//                        menuChangeLeft();
//                    }
//                }
//            }
//        });
//        iv_lock.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int eventaction = event.getAction();
//                final int x = (int) event.getX();
//                int y = (int) event.getY();
//                switch (eventaction){
//                    case MotionEvent.ACTION_DOWN: // touch down so check if the
//                        oldx = (int) event.getRawX();
//                        oldy = (int) event.getRawY();
//                        L.d("oldx==" + oldx);
//                        L.d("oldy==" + oldy);
//                        break;
//                    case MotionEvent.ACTION_MOVE: // touch drag with the ball
//
//                        ViewHelper.setScrollX(iv_lock, oldx - x);
//                        ViewHelper.setScrollY(iv_lock, oldy - y);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        ViewHelper.setScrollX(iv_lock, 0);
//                        ViewHelper.setScrollY(iv_lock, 0);
//                        if ((oldy - y) > (UserInfoUtil.getWindowHeight(LockActivity.this) / 4)) {
//                            //上滑解锁
//                            L.d("上画了滑了");
//                            finish();
//                            Intent i = new Intent();
//                            i.setAction("unlock");
//                            sendBroadcast(i);
//                        } else {
//                            if ((y - oldy) > (UserInfoUtil.getWindowHeight(LockActivity.this) / 4)) {
//                                //下滑
//                                runAction();
//                            }
//                        }
//                        if ((x - oldx) > (UserInfoUtil.getWindowWidth(LockActivity.this) / 3)) {
//                                L.d("右滑");
//                                goRight();
//                        } else {
//                                if ((oldx - x) > (UserInfoUtil.getWindowWidth(LockActivity.this) / 3)) {
//                                    //上报
//                                    L.d("左滑");
//                                    goLeft();
//                                }
//                        }
//                        break;
//                }
//                return true;
//            }
//        });
        lock_view3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                int eventaction = event.getAction();
                final int x = (int) event.getX();
                int y = (int) event.getY();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN: // touch down so check if the
                        oldx = (int) event.getX();
                        oldy = (int) event.getY();
                        L.d("oldx==" + oldx);
                        L.d("oldy==" + oldy);
                        break;
                    case MotionEvent.ACTION_MOVE: // touch drag with the ball

                        iv_on.setVisibility(View.INVISIBLE);


                        ViewHelper.setScrollX(test, oldx - x);
                        ViewHelper.setScrollY(test, oldy - y);
                        break;
                    case MotionEvent.ACTION_UP:


                        iv_on.setVisibility(View.VISIBLE);

                        upX = (int) event.getX();
                        upY = (int) event.getY();
                        Animator anim = ObjectAnimator.ofPropertyValuesHolder(lock_view3, AnimatorUtils.translationY(y - oldy, 0));
                        anim.setDuration(500);
                        Animator anim_end = ObjectAnimator.ofPropertyValuesHolder(lock_view3, AnimatorUtils.translationY(y - oldy, -UserInfoUtil.getWindowHeight(LockActivity.this)));
                        anim_end.setDuration(500);
                        anim_end.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                L.d("上画了滑了");


                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });


                        ViewHelper.setScrollX(test, 0);
                        ViewHelper.setScrollY(test, 0);


                        if (Math.abs(oldy - upY) > Math.abs(oldx - upX)) {
                            //上滑动
                            if ((oldy - y) > (UserInfoUtil.getWindowHeight(LockActivity.this) / 4)) {
                                //上滑解锁
                                L.d("上画了滑了");
                                finish();
                                overridePendingTransition(R.anim.fade, R.anim.hold);
                                Intent i = new Intent();
                                i.setAction("unlock");
                                sendBroadcast(i);

                            } else {
                                if ((y - oldy) > (UserInfoUtil.getWindowHeight(LockActivity.this) / 3)) {
                                    //下滑
                                    runAction();
                                }
                            }
                        } else {
                            //左右
                            if (oldy > UserInfoUtil.getWindowHeight(LockActivity.this) - UserInfoUtil.getWindowHeight(LockActivity.this) / 3) {
                                if ((x - oldx) > (UserInfoUtil.getWindowWidth(LockActivity.this) / 3)) {
                                    L.d("右滑");
                                    goRight();
                                } else {
                                    if ((oldx - x) > (UserInfoUtil.getWindowWidth(LockActivity.this) / 3)) {
                                        //上报
                                        L.d("左滑");
                                        goLeft();
                                    }
                                }
                            }
                        }
                }
                return true;
            }
        });


    }

    private void goRight() {
        int redPackets = (int) SPUtils.get(this, "lockpacket", 0);
        if (redPackets == 0) {
            Intent intent = new Intent(this, SendActivity.class);
            startActivity(intent);
            finish();
            SPUtils.put(this, "lockpacket", 1);
        } else if (redPackets == 1) {
            Intent intent = new Intent(this, DownloadTaskActivity.class);
            startActivity(intent);
            finish();
            SPUtils.put(this, "lockpacket", 2);
        } else if (redPackets == 2) {
            Intent intent = new Intent(this, InvateActivity.class);
            startActivity(intent);
            finish();
            SPUtils.put(this, "lockpacket", 3);
        } else if (redPackets == 3) {
            Intent intent = new Intent(this, SportActivity.class);
            startActivity(intent);
            finish();
            SPUtils.put(this, "lockpacket", 0);
        }
    }

    private void goLeft() {
        EventBus.getDefault().post(
                new ChangeTabType("lock"));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void runAction() {
        L.d("下滑了");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_phone:
                EventBus.getDefault().post(
                        new ChangeTabType("lock"));
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_hongbao:
//                Toast.makeText(getApplicationContext(), "暂未开放", Toast.LENGTH_SHORT).show();
                goRight();
                break;
        }
    }

    @Override
    protected void setStatusBar() {

        StatusBarUtil.setTranslucentForImageView(LockActivity.this, 0, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ad != null) {
            ad.stop();
        }

    }


    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    public boolean isExits(int ID) {
        File file1 = new File(Environment
                .getExternalStorageDirectory() + "/YX/lock/background" + ID + ".jpg");
        File file2 = new File(Environment
                .getExternalStorageDirectory() + "/YX/lock/phone" + ID + ".jpg");
        File file3 = new File(Environment
                .getExternalStorageDirectory() + "/YX/lock/red" + ID + ".jpg");
        File file4 = new File(Environment
                .getExternalStorageDirectory() + "/YX/lock/unlock" + ID + ".jpg");
        if (file1.exists() && file2.exists() && file3.exists() && file4.exists()) {
            return true;
        }
        return false;
    }
}
