package com.yunxi.phone.activity;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextPaint;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.Log;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.InvateBean;
import com.yunxi.phone.bean.InvateDataBean;
import com.yunxi.phone.bean.MsgBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.JDMediaScannerConnectionClient;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.MyShareUtils;
import com.yunxi.phone.utils.NetUtil;
import com.yunxi.phone.utils.ShareDialog;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/11.
 */
public class InvateActivity extends BaseActivity implements View.OnClickListener {
    AutoRelativeLayout back;
    TextView cloud_total;
    TextView cloud_out;
    TextView cloud_use;
    AutoRelativeLayout invate;
    AutoRelativeLayout user_info;
    String content = "新用户注册送300云朵！天天拿话费Q币！";
    String title = "";
    String url = "http://apics.qqb3.com/ceshi/download/index.html";
    MediaScannerConnection connection;
    InvateBean data;
    private Display display;
    private Dialog dialog;
    ACache mCache;
    Bitmap bitmap=null;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_invate;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        cloud_total = $(R.id.cloud_total);
        cloud_out = $(R.id.cloud_out);
        cloud_use = $(R.id.cloud_use);
        invate = $(R.id.invate);
        loading=$(R.id.loading);
        not_data=$(R.id.not_data);
        get_data=$(R.id.get_data);
        user_info=$(R.id.user_info);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        invate.setOnClickListener(this);
        user_info.setOnClickListener(this);
        get_data.setOnClickListener(this);
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        title="我的邀请ID:"+ACache.get(this).getAsString("user_id");
        getData();

    }

    private void getData() {
        Map<String, Object> map = new HashMap<>();
        showLoading();
        mCache = ACache.get(this);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        XUtil.Post(this, AddressApi.GET_INVITATION, map, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                hideLoading();
                L.i("邀请结果=====" + result.toString());

                InvateDataBean bean = JSON.parseObject(result,
                        InvateDataBean.class);
                not_data.setVisibility(View.GONE);
                if ("200".equals(bean.getResult())) {
                    data = bean.getData();
                    if (null != data) {
//                        new Thread().run();
//                        Bitmap bitmap = ImageUtil.getBitmap(data.getQr_url());
                        Glide.with(InvateActivity.this)
                                .load(data.getQrImg_Url())//
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                        InvateActivity.this.bitmap = bitmap;
                                    }
                                });
                    }
                    initView(data);
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(InvateActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();

                }else if("300".equals(bean.getResult())){

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(InvateActivity.this);

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
        MyAlertDialog dialog = new MyAlertDialog(InvateActivity.this)
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

                Intent login = new Intent(InvateActivity.this, LoginActivity.class);
                login.putExtra("from","loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }


    private void initView(InvateBean data) {
        cloud_total.setText(data.getCountlToday() + "");
        cloud_use.setText(data.getIntegralSum() + "");
        cloud_out.setText(data.getCountSum() + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.get_data:
                getData();
                break;
            case R.id.invate:
                //邀请
                ShareDialog dialog = new ShareDialog(InvateActivity.this);
                dialog.builder();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.addClickFriend(new ShareDialog.OnFriendListener() {
                    @Override
                    public void onClick() {
                        MyShareUtils.Share(InvateActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, umShareListener, content, "http"+data.getMy_Url().split("http")[1], title, getApplicationContext());
                    }
                });
                dialog.addClickQq(new ShareDialog.OnQqListener() {
                    @Override
                    public void onClick() {
                        MyShareUtils.Share(InvateActivity.this, SHARE_MEDIA.QQ, umShareListener, content,"http"+data.getMy_Url().split("http")[1], title, getApplicationContext());
                    }
                });
                dialog.addClickWeixin(new ShareDialog.OnWeixinListener() {
                    @Override
                    public void onClick() {
                        MyShareUtils.Share(InvateActivity.this, SHARE_MEDIA.WEIXIN, umShareListener, content, "http"+data.getMy_Url().split("http")[1], title, getApplicationContext());
                    }
                });
                dialog.addClickZone(new ShareDialog.OnZoneListener() {
                    @Override
                    public void onClick() {
                        MyShareUtils.Share(InvateActivity.this, SHARE_MEDIA.QZONE, umShareListener, content, "http"+data.getMy_Url().split("http")[1], title, getApplicationContext());
                    }
                });
                dialog.addClickWeibo(new ShareDialog.OnWeiboListener() {
                    @Override
                    public void onClick() {
                        MyShareUtils.Share(InvateActivity.this, SHARE_MEDIA.SINA, umShareListener, content, "http"+data.getMy_Url().split("http")[1], title, getApplicationContext());
                    }
                });
                dialog.addClickErwei(new ShareDialog.OnErweiListener() {
                    @Override
                    public void onClick() {
                        //二维码
                        //弹窗
                        showQr();
//
                    }
                });
                dialog.addClickYingyong(new ShareDialog.OnYingyongListener() {
                    @Override
                    public void onClick() {
                        //应用宝
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(data.getStore_Url());
                        Toast.makeText(getApplicationContext(), "已复制", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.addClickCopy(new ShareDialog.OnCopyListener() {
                    @Override
                    public void onClick() {
                        //复制
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(data.getMy_Url());
                        Toast.makeText(getApplicationContext(), "已复制", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                break;
            case R.id.user_info:
                Intent intent = new Intent(getApplicationContext(), InvateDetailActivity.class);
                startActivity(intent);
                break;
        }
    }
    View view;
    AutoLinearLayout view_bg;
    private void showQr() {
        WindowManager windowManager = (WindowManager) getApplication()
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();


        view = LayoutInflater.from(this).inflate(
                R.layout.qr_dialog, null);
        ImageView iv_qr = (ImageView) view.findViewById(R.id.iv_qr);

        view_bg = (AutoLinearLayout) view.findViewById(R.id.view_bg);
        if (null!=data) {
//           Glide.with(this).load(data.getQq_url()).into(iv_qr);
//            iv_qr.setImageURI(Uri.parse(data.getQq_url()));
//            iv_qr.setBackgroundResource(R.mipmap.download);
            iv_qr.setImageBitmap(bitmap);
        }
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        TextView tv_userid = (TextView) view.findViewById(R.id.tv_userid);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        AutoRelativeLayout save = (AutoRelativeLayout) view.findViewById(R.id.login_btn);
        tv_userid.setText("邀请ID : " + mCache.getAsString("user_id"));
        TextPaint paint = tv_title.getPaint();
        paint.setFakeBoldText(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=data){
//                    downImage(data);

                    saveLayout();
                    dialog.dismiss();

                }
            }
        });
        dialog.show();
    }
    String TempFilePath;
    private void saveLayout() {
        TempFilePath = Environment
                .getExternalStorageDirectory() + "/YX/pic/" + "invatate.jpg";
       final File file = new File(TempFilePath);
        final Bitmap bmp = Bitmap.createBitmap(view_bg.getWidth(), view_bg.getHeight(), Bitmap.Config.ARGB_8888);
        view_bg.draw(new Canvas(bmp));
        savePicture(bmp, "invatate.jpg");// 保存图片
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            LoadShare(platform+"");
            Toast.makeText(InvateActivity.this, platform + " 分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(InvateActivity.this, platform + " 分享失败", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(InvateActivity.this, platform + " 分享取消", Toast.LENGTH_SHORT).show();
        }
    };

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
    FileOutputStream fos;
    public void savePicture(Bitmap bm, String fileName) {

        if (bm == null) {

             Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
            return;
        }
        File foder = new File(Environment
                .getExternalStorageDirectory() + "/YX/pic/");
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(foder, fileName);
        try {
//            if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
//            }
            fos = new FileOutputStream(myCaptureFile);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();


        File newFile = new File(TempFilePath);
        JDMediaScannerConnectionClient connectionClient = new JDMediaScannerConnectionClient(newFile);

        connection = new MediaScannerConnection(getApplicationContext(), connectionClient);
        connectionClient.setMediaScannerConnection(connection);
        connection.connect();
    }

    public void LoadShare(String platform) {
        Map<String, Object> map = new HashMap<>();
        mCache = ACache.get(this);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        map.put("type",platform);
        XUtil.Post(this, AddressApi.ADD_INVITATION, map, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                L.i("邀请回调=====" + result.toString());
                MsgBean bean = JSON.parseObject(result,
                        MsgBean.class);

                if ("200".equals(bean.getResult())) {

                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(InvateActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }else if("300".equals(bean.getResult())){


                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(InvateActivity.this);

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

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }
}
