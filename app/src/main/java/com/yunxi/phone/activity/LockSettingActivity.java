package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.LockImageAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.LockBean;
import com.yunxi.phone.bean.LockListBean;
import com.yunxi.phone.eventtype.LockType;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.SPUtils;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/24.
 */
public class LockSettingActivity extends BaseActivity implements View.OnClickListener  {
    private AutoRelativeLayout back;
    private GridView gv_lock;
    public static int LOCK_ID=0;
    LockBean bean;
    LockImageAdapter adapter;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;
    private AutoRelativeLayout data_zero;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock_setting;
    }

    @Override
    protected void findById() {
        back=$(R.id.back);
        gv_lock=$(R.id.gv_lock);
        loading = $(R.id.loading);
        not_data = $(R.id.not_data);
        data_zero = $(R.id.data_zero);
        get_data = $(R.id.get_data);
    }

    @Override
    protected void regListener() {
        EventBus.getDefault().register(this);
        back.setOnClickListener(this);
        get_data.setOnClickListener(this);
        gv_lock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data.get(position).getID() != LOCK_ID) {
                    Intent intent = new Intent(getApplicationContext(), MySingleWallActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", bean.getData().get(position));
                    bundle.putInt("position", position);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

            //获取使用中的壁纸
        LOCK_ID =  (int) SPUtils.get(this, "isUse", 0);
        if(LOCK_ID!=0){
            if(!isExits(LOCK_ID)){
                SPUtils.put(getApplicationContext(),"isUse",0);
                LOCK_ID=0;
            }
        }
        showLoading();
        getData();
    }


    List<LockListBean> data=new ArrayList<LockListBean>();
    private void getData() {
        Map<String, Object> map = new HashMap<>();

        ACache mCache = ACache.get(this);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        XUtil.Post(LockSettingActivity.this, AddressApi.GET_LOCK, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                L.d(result);
hideLoading();
                not_data.setVisibility(View.GONE);
                bean = JSON.parseObject(result,
                        LockBean.class);
                if ("200".equals(bean.getResult())) {
                    LockListBean defualt = new LockListBean();
                    defualt.setID(0);
                    data = bean.getData();
                    data.add(0, defualt);
                    adapter = new LockImageAdapter(LockSettingActivity.this, data);
                    gv_lock.setAdapter(adapter);
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(LockSettingActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                } else if ("300".equals(bean.getResult())) {


                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(LockSettingActivity.this);

                    String phone = mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check", "0");
                    mCache.put("phone", "");

                    //激光注销掉
                    JPushInterface.setAlias(getApplicationContext(), "0", new TagAliasCallback() {
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
            not_data.setVisibility(View.VISIBLE);
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
        MyAlertDialog dialog = new MyAlertDialog(LockSettingActivity.this)
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

                Intent login = new Intent(LockSettingActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }



    public void onEventMainThread(LockType event){
        LOCK_ID =  (int) SPUtils.get(this, "isUse", 0);
//        LockListBean defualt = new LockListBean();
//        defualt.setID(0);
//        bean.getData().add(0, defualt);

//        List<LockListBean> list =new ArrayList<>();
//        list.addAll(data);
        adapter = new LockImageAdapter(LockSettingActivity.this,bean.getData());
        gv_lock.setAdapter(adapter);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.get_data:
                showLoading();
                getData();
                break;
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
        EventBus.getDefault().unregister(this);
    }
    public boolean isExits(int ID){
        File file1=new File(Environment
                .getExternalStorageDirectory() + "/YX/lock/background"+ID+".jpg");
        File file2=new File(Environment
                .getExternalStorageDirectory() + "/YX/lock/phone"+ID+".jpg");
        File file3=new File(Environment
                .getExternalStorageDirectory() + "/YX/lock/red"+ID+".jpg");
        File file4=new File(Environment
                .getExternalStorageDirectory() + "/YX/lock/unlock"+ID+".jpg");
        if(file1.exists()&&file2.exists()&&file3.exists()&&file4.exists())
        {
            return true;
        }
        return false;
    }


    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }


    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}
