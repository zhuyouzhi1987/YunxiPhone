package com.yunxi.phone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.makeramen.roundedimageview.RoundedImageView;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.CheckFaceBean;
import com.yunxi.phone.bean.CheckIdCardBean;
import com.yunxi.phone.bean.CompareFaceBean;
import com.yunxi.phone.bean.MsgBean;
import com.yunxi.phone.eventtype.LogoutCloseType;
import com.yunxi.phone.eventtype.YanZhengType;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.ImageUtil;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MyAlertDialog;
import com.yunxi.phone.utils.SelectDialog;
import com.yunxi.phone.utils.SingleButtonAlertDialog;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;

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
 * Created by Administrator on 2017/1/19.
 */
public class CertificationActivity extends BaseActivity implements View.OnClickListener {

    private AutoRelativeLayout back;
    private ImageView iv_positive;
    //    private ImageView iv_back;
    private RoundedImageView iv_back;

    private String id_card_path = "";
    private String face_path = "";

    private AutoRelativeLayout yanzheng_btn;
    private AutoLinearLayout loading;
    private AutoRelativeLayout not_data;
    private AutoRelativeLayout get_data;

    private String id_name, id_gender, id_number;

    private static final String LOGIN_PREFERENCE = "login_preferences";

    private static final String KEY_IS_LOGIN = "isLogin";
    private SharedPreferences mSharedPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_certifition;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        iv_positive = $(R.id.iv_positive);
        iv_back = $(R.id.face_img);
        yanzheng_btn = $(R.id.yanzheng_btn);
        loading = $(R.id.loading);
        not_data = $(R.id.not_data);
        get_data = $(R.id.get_data);
    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
        iv_positive.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        yanzheng_btn.setOnClickListener(this);
        get_data.setOnClickListener(this);
    }

    @Override
    protected void init() {

        mSharedPreferences = getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.iv_positive:
//                selectPositivePic();
                showPositive();
                break;
            case R.id.face_img:
//                selectBackPic();
                showBack();
                break;
            case R.id.yanzheng_btn:
                if (!"".equals(id_card_path) && !"".equals(face_path)) {

                    yanzheng_btn.setOnClickListener(null);
                    showLoading();
                    compareFace(id_card_path, face_path);

                } else {
                    Toast.makeText(CertificationActivity.this, "请将图片上传完整", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.get_data:
                showLoading();
                compareFace(id_card_path, face_path);
                break;


        }
    }

    private void showBack() {
        SelectDialog dialog = new SelectDialog(this);
        dialog.builder();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.addClickPhoto(new SelectDialog.OnPhotoListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 5);
            }
        });
        dialog.addClickGraph(new SelectDialog.OnGraphListener() {
            @Override
            public void onClick() {
                //相机
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                    String out_file_path = Environment.getExternalStorageDirectory() + "/yunxi/portrait/";
                    File dir = new File(out_file_path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String capturePath = out_file_path + "back.jpg";
                    File file = new File(capturePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(getImageByCamera, 4);
                } else {
                    Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.addClickCancle(new SelectDialog.OnCancleListener() {
            @Override
            public void onClick() {
                //取消
            }
        });
        dialog.show();
    }

    private void showPositive() {
        SelectDialog dialog = new SelectDialog(this);
        dialog.builder();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.addClickPhoto(new SelectDialog.OnPhotoListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
        dialog.addClickGraph(new SelectDialog.OnGraphListener() {
            @Override
            public void onClick() {
                //相机
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                    String out_file_path = Environment.getExternalStorageDirectory() + "/yunxi/portrait/";
                    File dir = new File(out_file_path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String capturePath = out_file_path + "positive.jpg";
                    File file = new File(capturePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(getImageByCamera, 2);
                } else {
                    Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.addClickCancle(new SelectDialog.OnCancleListener() {
            @Override
            public void onClick() {
                //取消
            }
        });
        dialog.show();
    }


    // 检查是不是身份证照片
    private void checkIdCard(final String img_path) {

        Map<String, Object> map = new HashMap<>();

        map.put("api_key", AddressApi.API_KEY);
        map.put("api_secret", AddressApi.API_SECRET);

//        final String img_temp = ImageUtil.compressImage(img_path, img_path);

        map.put("image_file", new File(img_path));


        XUtil.Post3(AddressApi.ID_CARD_URL, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                CheckIdCardBean bean = JSON.parseObject(result,
                        CheckIdCardBean.class);

                if (bean.getCards().size() > 0) {

                    if ("back".equals(bean.getCards().get(0).getSide())) {

                        showWarnDialog("您上传的身份证是反面 , 请上传正面照片 !", 1);
                        id_card_path = "";

                    } else if ("front".equals(bean.getCards().get(0).getSide())) {
                        id_card_path = img_path;
                        id_name = bean.getCards().get(0).getName();
                        id_gender = bean.getCards().get(0).getGender();
                        id_number = bean.getCards().get(0).getId_card_number();

                    }


                } else {

                    showWarnDialog("您上传的身份证无法识别 , 请检查拍摄设备/光线/图片不完整等问题 !", 1);
                    id_card_path = "";
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {


                if (ex instanceof HttpException) {

                    HttpException httpEx = (HttpException) ex;

                    Toast.makeText(CertificationActivity.this, "错误" + httpEx.getCode() + " , 请重新上传", Toast.LENGTH_SHORT).show();

                    id_card_path = "";

                } else {
                    Toast.makeText(CertificationActivity.this, "未知错误 , 请重新上传", Toast.LENGTH_SHORT).show();
                    id_card_path = "";
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    // 检查人脸
    private void checkFace(final String img_path) {

        Map<String, Object> map = new HashMap<>();

        map.put("api_key", AddressApi.API_KEY);
        map.put("api_secret", AddressApi.API_SECRET);

//        final String img_temp = ImageUtil.compressImage(img_path, img_path);

        map.put("image_file", new File(img_path));


        XUtil.Post3(AddressApi.FACE_DETECT_URL, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                CheckFaceBean bean = JSON.parseObject(result,
                        CheckFaceBean.class);

                if (bean.getFaces().size() > 0) {

                    face_path = img_path;

                } else {

                    showWarnDialog("您上传的人脸图片无法识别 , 请使用本人头像或检查拍摄设备/光线等问题 !", 2);
                    face_path = "";
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {


                if (ex instanceof HttpException) {

                    HttpException httpEx = (HttpException) ex;

                    Toast.makeText(CertificationActivity.this, "错误" + httpEx.getCode() + " , 请重新上传", Toast.LENGTH_SHORT).show();

                    face_path = "";

                } else {
                    Toast.makeText(CertificationActivity.this, "未知错误 , 请重新上传", Toast.LENGTH_SHORT).show();
                    face_path = "";
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    //对比身份证和人脸照片
    private void compareFace(final String first_path, final String second_path) {

        Map<String, Object> map = new HashMap<>();

        map.put("api_key", AddressApi.API_KEY);
        map.put("api_secret", AddressApi.API_SECRET);

        map.put("image_file1", new File(first_path));
        map.put("image_file2", new File(second_path));


        XUtil.Post3(AddressApi.FACE_COMPARE_URL, map, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                yanzheng_btn.setOnClickListener(CertificationActivity.this);

                CompareFaceBean bean = JSON.parseObject(result,
                        CompareFaceBean.class);


                L.d("相似度==" + bean.getConfidence());

                if (bean.getConfidence() > 50) {

                    //去掉自己的图片上传接口

                    sendCard(first_path, second_path);

                } else {
                    hideLoading();
                    showWarnDialog("您上传的身份证与人脸经验证相似度低于50% ， 请使用本人免冠照或检查身份证图片质量！", 3);
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
                yanzheng_btn.setOnClickListener(CertificationActivity.this);

                if (ex instanceof HttpException) {

                    HttpException httpEx = (HttpException) ex;

                    Toast.makeText(CertificationActivity.this, "错误" + httpEx.getCode() + " , 请重试", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(CertificationActivity.this, "未知错误 , 请重试", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
                hideLoading();
                yanzheng_btn.setOnClickListener(CertificationActivity.this);

            }

            @Override
            public void onFinished() {
                hideLoading();
                yanzheng_btn.setOnClickListener(CertificationActivity.this);

            }
        });

    }

    List<File> listFile = new ArrayList<File>();

    private void sendCard(String first_path, String second_path) {
        listFile.clear();
        yanzheng_btn.setEnabled(false);
        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(this);
        String first = ImageUtil.compressImage(first_path, first_path);
        String second = ImageUtil.compressImage(second_path, second_path);
        File firstFile = new File(first);
        File secondFile = new File(second);
        listFile.add(firstFile);
        listFile.add(secondFile);
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        try {
            map.put("name", java.net.URLEncoder.encode(id_name, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if ("男".equals(id_gender)) {
            map.put("sex", 1);
        } else if ("女".equals(id_gender)) {
            map.put("sex", 0);
        }
        map.put("number", id_number);


        XUtil.UpLoadFile(CertificationActivity.this, AddressApi.YANZHENG, map, listFile, true, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                L.d(result);
                hideLoading();
                yanzheng_btn.setEnabled(true);
                MsgBean bean = JSON.parseObject(result,
                        MsgBean.class);
                if ("200".equals(bean.getResult())) {

                    EventBus.getDefault().post(
                            new YanZhengType(""));

                    ACache mCache = ACache.get(CertificationActivity.this);
                    mCache.put("check", "1");

                    //清空未读集合
                    Toast.makeText(CertificationActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                    finish();
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(CertificationActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
                } else if ("300".equals(bean.getResult())) {
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putBoolean(KEY_IS_LOGIN, false);
                    e.commit();

                    ACache mCache = ACache.get(CertificationActivity.this);

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
                yanzheng_btn.setEnabled(true);
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


    private void showWarnDialog(String phone) {
        MyAlertDialog dialog = new MyAlertDialog(CertificationActivity.this)
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

                Intent login = new Intent(CertificationActivity.this, LoginActivity.class);
                login.putExtra("from", "loading");
                startActivity(login);

                EventBus.getDefault().post(
                        new LogoutCloseType("close"));


            }
        });

        dialog.show();
    }


    private void showWarnDialog(String msg, final int type) {
        SingleButtonAlertDialog dialog = new SingleButtonAlertDialog(CertificationActivity.this)
                .builder().setMsg(msg)
                .setTitle("系统提示")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type == 1) {


                            showPositive();


                        } else if (type == 2) {

                            showBack();

                        } else if (type == 3) {


                        }


                    }
                });

        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String path = selectedImage.getPath();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            try {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                path = cursor.getString(columnIndex);
                cursor.close();
            } catch (Exception e) {
            }
            String img_temp = ImageUtil.compressImage(path, path);
            iv_positive.setImageURI(Uri.parse(img_temp));
            checkIdCard(img_temp);


        } else if (requestCode == 2 && resultCode == RESULT_OK) {

            iv_positive.setImageURI(null);
//            String img_temp =ImageUtil.saveBitmapToFile(new File(Environment.getExternalStorageDirectory() + "/yunxi/portrait/positive.jpg"), Environment.getExternalStorageDirectory() + "/yunxi/portrait/positiv.jpg");
            String img_temp = ImageUtil.compressImage(Environment.getExternalStorageDirectory() + "/yunxi/portrait/positive.jpg", Environment.getExternalStorageDirectory() + "/yunxi/portrait/positive.jpg");
            iv_positive.setImageURI(Uri.parse(img_temp));
            checkIdCard(img_temp);
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/pf/portrait/temp.jpg")));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else if (requestCode == 5 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String path = selectedImage.getPath();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            try {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                path = cursor.getString(columnIndex);
                cursor.close();
            } catch (Exception e) {
            }
            String img_temp = ImageUtil.compressImage(path, path);
            iv_back.setImageURI(Uri.parse(img_temp));

            checkFace(img_temp);

        } else if (requestCode == 4 && resultCode == RESULT_OK) {
            iv_back.setImageURI(null);
            String img_temp = ImageUtil.compressImage(Environment.getExternalStorageDirectory() + "/yunxi/portrait/back.jpg", Environment.getExternalStorageDirectory() + "/yunxi/portrait/back.jpg");
            iv_back.setImageURI(Uri.parse(img_temp));

            checkFace(img_temp);
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
