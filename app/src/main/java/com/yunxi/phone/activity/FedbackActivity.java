package com.yunxi.phone.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.MsgBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.ImageUtil;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.NetUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/13.
 */
public class FedbackActivity extends BaseActivity implements View.OnClickListener {
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private AutoRelativeLayout back;
    private ImageView iv_add;
    private AutoRelativeLayout login_btn;
    private EditText et_content;
    private TextView tv_sum;
    String path;
    String resPath = Environment
            .getExternalStorageDirectory() + "/yunxi/apps/img";
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;


    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fedback;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        iv_add = $(R.id.iv_add);
        login_btn = $(R.id.login_btn);
        et_content = $(R.id.et_content);
        tv_sum = $(R.id.tv_sum);
        not_data = $(R.id.not_data);
        get_data = $(R.id.get_data);
        loading = $(R.id.loading);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_sum.setText(s.length() + "/120");
            }
        });
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        tv_sum.setText("0/120");
    }

    boolean flag = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.get_data:
                sendFed();
                break;
            case R.id.iv_add:
                pickImage();
                break;
            case R.id.login_btn:
                if (TextUtils.isEmpty(et_content.getText().toString().trim())) {
                    Toast.makeText(FedbackActivity.this, "请填写内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendFed();
                break;
        }
    }

    File file;
    List<File> listFile = new ArrayList<File>();

    private void sendFed() {
        login_btn.setEnabled(false);
        showLoading();
        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(this);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        try {
            map.put("content", java.net.URLEncoder.encode(et_content.getText().toString().trim(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (flag) {
            String img_temp = ImageUtil.compressImage(path, path);
            file = new File(img_temp);
            listFile.add(file);
        }

        XUtil.UpLoadFile(FedbackActivity.this, AddressApi.FEED_BACK, map, listFile, flag, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                L.d(result);
                hideLoading();
                login_btn.setEnabled(true);
                not_data.setVisibility(View.GONE);
                MsgBean bean = JSON.parseObject(result,
                        MsgBean.class);
                if ("200".equals(bean.getResult())) {
                    //清空未读集合
                    Toast.makeText(FedbackActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                    finish();
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(FedbackActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }else if("300".equals(bean.getResult())){



                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(FedbackActivity.this);

                    String phone =mCache.getAsString("phone");

                    mCache.put("user_id", "0");
                    mCache.put("token", "0");
                    mCache.put("check","0");
                    mCache.put("phone","");

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
                login_btn.setEnabled(true);
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
        MyAlertDialog dialog = new MyAlertDialog(FedbackActivity.this)
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

                Intent login = new Intent(FedbackActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }


    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    "浏览图片需要您提供浏览存储的权限",
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle("权限拒绝")
                    .setMessage(rationale)
                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(FedbackActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton("拒绝", null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            path = selectedImage.getPath();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            try {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                path = cursor.getString(columnIndex);
                flag = true;
                cursor.close();
            } catch (Exception e) {
            }
            iv_add.setImageURI(Uri.parse(ImageUtil.compressImage(path,path)));
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

    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}
