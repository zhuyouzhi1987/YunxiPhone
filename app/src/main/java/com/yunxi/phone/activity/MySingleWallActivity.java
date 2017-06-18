package com.yunxi.phone.activity;

import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.LockInfo;
import com.yunxi.phone.bean.LockListBean;
import com.yunxi.phone.eventtype.LockType;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.SPUtils;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by liufeng on 16/8/16.
 */
public class MySingleWallActivity extends BaseActivity implements View.OnClickListener {
    private AutoRelativeLayout back;
    private ImageView sdv_big;
    private AutoRelativeLayout use_wall;
    TextView imageViewBack;
    TextView tv_hint;
    private LockListBean data;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_single_wall;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        sdv_big = $(R.id.sdv_big);
        use_wall = $(R.id.use_wall);
        tv_hint = $(R.id.tv_hint);
    }

    @Override
    protected void regListener() {

        back.setOnClickListener(this);
        use_wall.setOnClickListener(this);
    }

    int flag = 0;

    @Override
    protected void init() {

        data = (LockListBean) getIntent().getSerializableExtra("data");
        int position = getIntent().getIntExtra("position", -1);

        //监测本地是已经下载过了 如果存在显示确认下载
        if (position == 0) {
            tv_hint.setText("更换壁纸");
            flag = 1;
            sdv_big.setImageResource(R.mipmap.lock_bg);
            return;
        } else {
            boolean exits = isExits(data.getID());
            if (exits) {
                tv_hint.setText("更换壁纸");
                flag = 1;
            } else {
                tv_hint.setText("下载壁纸");
                flag = 0;
            }
        }

        Glide.with(this).load(data.getThumbnail_Img()).centerCrop().dontAnimate().placeholder(R.mipmap.news_placeholder).into(sdv_big);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    List<String> list;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.use_wall:
                //看是否下载
                if (flag == 0) {
                    //需要下载
                    down();
                } else if (flag == 1) {
                    //已经下载直接替换 修改使用状态 发送消息
                    SPUtils.put(getApplicationContext(), "isUse", data.getID());
                    EventBus.getDefault().post(
                            new LockType(""));
                    tv_hint.setText("更换成功");
                    use_wall.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                    use_wall.setEnabled(false);
                }
                break;
        }
    }

    LockInfo info;
    String name;
    int finish = 0;

    private void down() {
        use_wall.setEnabled(false);
        info = new LockInfo();
        list = new ArrayList<String>();
        list.clear();
        list.add(data.getBackground_Img());
        list.add(data.getPhone_Img());
        list.add(data.getRedEnvelop_Img());
        list.add(data.getUnlock_Img());
        for (int x = 0; x < list.size(); x++) {
            if (x == 0) {
                name = "background" + data.getID() + ".jpg";
            } else if (x == 1) {
                name = "phone" + data.getID() + ".jpg";
            } else if (x == 2) {
                name = "red" + data.getID() + ".jpg";
            } else if (x == 3) {
                name = "unlock" + data.getID() + ".jpg";
            }
            String TempFilePath = Environment
                    .getExternalStorageDirectory() + "/YX/lock/" + name;
            final int finalX = x;
            XUtil.DownLoadFile(list.get(x), TempFilePath, new Callback.ProgressCallback<File>() {
                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {
                    L.d("avonStartedaa");
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    L.d(total + "------" + current + "aaa");
                    tv_hint.setText("下载中 (" + (finish + 1) + "/4)");
                    use_wall.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                    use_wall.setEnabled(false);
                }

                @Override
                public void onSuccess(File result) {
                    L.d(result.getName());
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    tv_hint.setText("重新下载");
                    use_wall.setBackgroundColor(getResources().getColor(R.color.appMainColor));
                    use_wall.setEnabled(true);
                    return;

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    L.d("onFinished");
                    finish++;
                    if (finish == 4) {
                        //已经全部下载完成
                        SPUtils.put(getApplicationContext(), "isUse", data.getID());
                        EventBus.getDefault().post(
                                new LockType(""));
                        use_wall.setBackgroundColor(getResources().getColor(R.color.gry_btn));
                        use_wall.setEnabled(false);
                        flag = 1;
                        tv_hint.setText("更换成功");
                        //下载完成
                        try {
                            info.setLockId(data.getID());
                            YunxiApplication.db.save(info);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
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
