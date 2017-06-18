package com.yunxi.phone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.base.BaseActivity;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by bond on 16/2/19.
 */
public class SendDescActivity extends BaseActivity implements View.OnClickListener{

    private AutoRelativeLayout back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.send_desc;
    }

    @Override
    protected void findById() {
        back=$(R.id.back);

    }

    @Override
    protected void regListener() {
        back.setOnClickListener(this);
    }

    @Override
    protected void init() {


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:

            finish();

                break;




        }
    }
}
