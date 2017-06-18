package com.yunxi.phone.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.Contact;
import com.yunxi.phone.bean.RecordBean;
import com.yunxi.phone.utils.ACache;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;

public class AddContactActivity extends BaseActivity implements View.OnClickListener {

    private AutoRelativeLayout back;
    private EditText name_text;
    private EditText phone_text;
    private EditText remark_text;
    private AutoRelativeLayout reg_btn;
    String phone;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_contact;
    }

    @Override
    protected void findById() {
        back = $(R.id.back);
        name_text = $(R.id.name_text);
        phone_text = $(R.id.phone_text);
        remark_text = $(R.id.remark_text);
        reg_btn = $(R.id.reg_btn);
    }

    @Override
    protected void regListener() {
        name_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (!TextUtils.isEmpty(phone_text.getText().toString())) {
                        reg_btn.setEnabled(true);
                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_yuanjiao_itemclick_selector));
                    } else {
                        reg_btn.setEnabled(false);
                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.gry_yuanjiao_itemclick_selector));
                    }
                } else {
//                    if (!TextUtils.isEmpty(phone_text.getText().toString())) {
//                        reg_btn.setEnabled(true);
//                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_yuanjiao));
//                    } else {
//                        reg_btn.setEnabled(false);
//                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.gry_yuanjiao));
//                    }
                    reg_btn.setEnabled(false);
                    reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.gry_yuanjiao_itemclick_selector));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (!TextUtils.isEmpty(name_text.getText().toString())) {
                        //打开按钮
                        reg_btn.setEnabled(true);
                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_yuanjiao_itemclick_selector));
                    } else {
                        reg_btn.setEnabled(false);
                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.gry_yuanjiao_itemclick_selector));
                    }
                } else {
//                    if (!TextUtils.isEmpty(name_text.getText().toString())) {
//                        //打开按钮
//                        reg_btn.setEnabled(true);
//                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_yuanjiao));
//                    } else {
//                        reg_btn.setEnabled(false);
//                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.gry_yuanjiao));
//                    }
                    reg_btn.setEnabled(false);
                    reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.gry_yuanjiao_itemclick_selector));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg_btn.setEnabled(false);
                testAddContacts2(name_text.getText().toString(), phone_text.getText().toString(), remark_text.getText().toString());

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    int position;

    @Override
    protected void init() {
        reg_btn.setEnabled(false);
        phone = getIntent().getStringExtra("phone");
        position = getIntent().getIntExtra("position", -1);
        if (!TextUtils.isEmpty(phone)) {
            phone_text.setText(phone + "");
            phone_text.setEnabled(false);
            phone_text.setHintTextColor(getResources().getColor(R.color.textColor));
        }
    }

    @Override
    public void onClick(View v) {

    }

    boolean tag1 = false;

    public void testAddContacts2(String name, String phone, String remark) {
//        ContactsManager cm = new ContactsManager(getApplicationContext(), getContentResolver());
        long timeID = System.currentTimeMillis();
        ACache mCache = ACache.get(this);
        String contactjson = mCache.getAsString("contact");
        List<Contact> cacheList = JSON.parseArray(contactjson, Contact.class);
        for (Contact contactBean : cacheList) {
            if (contactBean.getNumber().equals(phone)) {
                //有重复号码
                Toast.makeText(AddContactActivity.this, "添加失败，号码已存在", Toast.LENGTH_SHORT).show();
                reg_btn.setEnabled(true);
                return;
            }
        }

        Contact contact = new Contact();
        contact.setName(name);
        contact.setNumber(phone + "");
        contact.setNote(remark + "");
        contact.setId(timeID + "");
        cacheList.add(contact);
        String jsonList = JSON.toJSONString(cacheList);
        mCache.put("contact", jsonList);


        String recordjson = mCache.getAsString("record");
        List<RecordBean> recordList = JSON.parseArray(recordjson, RecordBean.class);
        if (position != -1) {
            //更新通话记录
            for (RecordBean recordBean : recordList) {
                if (phone.equals(recordBean.getNumber())) {
                    recordBean.setName(name);
                    recordBean.setId(timeID + "");
                    recordBean.setIsExite(true);
                    recordBean.setNote(remark + "");
                }
            }

//          RecordBean recordBean = recordList.get(position);
//          recordBean.setIsExite(true);
//          recordBean.setName(name);
//            recordBean.setNumber(phone + "");
//            recordBean.setNote(remark + "");
//            recordBean.setId(id+"");
//            recordList.remove(position);
//            recordList.add(position, recordBean);
        }else{
            for (RecordBean recordBean : recordList) {
                if (phone.equals(recordBean.getNumber())) {
                    recordBean.setName(name);
                    recordBean.setId(timeID + "");
                    recordBean.setIsExite(true);
                    recordBean.setNote(remark + "");
                }
            }
        }
        String recordjsonList = JSON.toJSONString(recordList);
        mCache.put("record", recordjsonList);
        finish();
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
}

