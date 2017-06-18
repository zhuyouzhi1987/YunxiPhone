package com.yunxi.phone.activity;

import android.content.Intent;
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

/**
 * Created by Administrator on 2017/1/4.
 */
public class UpDateContactActivity extends BaseActivity {
    private AutoRelativeLayout back;
    private EditText name_text;
    private EditText phone_text;
    private EditText remark_text;
    private AutoRelativeLayout reg_btn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_contact;
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
                }else{
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
                }else{
//                    if (!TextUtils.isEmpty(name_text.getText().toString())) {
//                        //打开按钮
//                        reg_btn.setEnabled(true);
//                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.gry_yuanjiao));
//                    } else {
//                        reg_btn.setEnabled(false);
//                        reg_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_yuanjiao));
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
//                ContactsManager cm = new ContactsManager(UpDateContactActivity.this,getContentResolver());
                ACache mCache = ACache.get(getApplication());
                List<Contact> contactList = JSON.parseArray(mCache.getAsString("contact"), Contact.class);
                for (Contact contact1 : contactList) {
                    if(contact1.getNumber().equals(phone)){
                        continue;
                    }
                    if(contact1.getNumber().equals(phone_text.getText().toString() + "")){
                        Toast.makeText(UpDateContactActivity.this, "编辑失败，号码已存在", Toast.LENGTH_SHORT).show();
                        reg_btn.setEnabled(true);
                        return;
                    }
                }
                for (Contact contact1 : contactList) {
                    if (contact1.getId().equals(id)) {
                        contact1.setName(name_text.getText().toString());
                        contact1.setNumber(phone_text.getText().toString() + "");
                        contact1.setNote(remark_text.getText().toString() + "");
                        String contactJson = JSON.toJSONString(contactList);
                        mCache.put("contact", contactJson);
                        break;
                    }
                }

//                Contact contact = new Contact();
//                contact.setName(name_text.getText().toString());
//                contact.setNumber(phone_text.getText().toString() + "");
//                contact.setNote(remark_text.getText().toString() + "");
//                cm.updateContact(id, contact);


                List<RecordBean> record = JSON.parseArray(mCache.getAsString("record"), RecordBean.class);
                for(RecordBean bean : record){
                    if(bean.getId().equals(id)){
                        bean.setName(name_text.getText().toString());
                        bean.setNumber(phone_text.getText().toString() + "");
                        bean.setNote(remark_text.getText().toString() + "");
                        String recordList = JSON.toJSONString(record);
                        mCache.put("record", recordList);
                        Intent intent = new Intent();
                        intent.putExtra("record",recordList);
                        setResult(0, intent);
                    }
                }


                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    String id;
    String name;
    String phone;
    String note;
    int position;
    int flag;
    @Override
    protected void init() {
      //设置默认
        Intent intent = getIntent();
        if(intent!=null){
            id = intent.getExtras().getString("id");
            name = intent.getExtras().getString("name");
            phone = intent.getExtras().getString("phone");
            note = intent.getExtras().getString("note");
            flag = intent.getExtras().getInt("flag");
            position = intent.getExtras().getInt("position");
        }
        initView();
    }

    private void initView() {
        name_text.setText(name);
        phone_text.setText(phone);
        remark_text.setText(note);
        if(flag==0){
            //suosi
            phone_text.setText(phone + "");
            phone_text.setEnabled(false);
            phone_text.setHintTextColor(getResources().getColor(R.color.textColor));
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
