package com.yunxi.phone.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.RewardAdapter;
import com.yunxi.phone.base.BaseActivity;
import com.yunxi.phone.bean.ProportionBean;
import com.yunxi.phone.bean.RunDataBean;
import com.yunxi.phone.bean.RunningBaseBean;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/10.
 */
public class RewardRulesActivity extends BaseActivity  implements View.OnClickListener{
    ListView list;
    AutoRelativeLayout back;
    ArrayList<RunningBaseBean> runningbase;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_reward_rules;
    }
    @Override
    protected void findById() {
        back=$(R.id.back);
        list=$(R.id.list);
    }
    @Override
    protected void regListener() {
        back.setOnClickListener(this);
    }
    @Override
    protected void init() {
        RunDataBean runData = (RunDataBean) getIntent().getSerializableExtra(
                "data");
        if(null!=runData){
            runningbase = runData.getRunningbase();
            ProportionBean proportion = runData.getProportion();
            RunningBaseBean bean = new RunningBaseBean();
            bean.setRun_Steps(proportion.getCloudNumber());
            bean.setRun_Integral(proportion.getPrize());
            runningbase.add(bean);
        }


        RewardAdapter mAdapter=new RewardAdapter(RewardRulesActivity.this);
        mAdapter.addAll(runningbase);
        list.setAdapter(mAdapter);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
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
}
