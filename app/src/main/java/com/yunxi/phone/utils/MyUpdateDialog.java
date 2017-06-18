package com.yunxi.phone.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.yunxi.phone.R;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.Serializable;


public class MyUpdateDialog{
    private Context context;
    private Dialog dialog;
    private AutoLinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView txt_msg;

    private TextView btn_cancel;
    private TextView btn_ok;
    private TextView now;
    private Display display;
    AutoRelativeLayout qiangzhi;
    AutoLinearLayout no_qiangzhi;

    public MyUpdateDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public MyUpdateDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.toast_view_update_alertdialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (AutoLinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);


        txt_msg = (TextView) view.findViewById(R.id.txt_msg);

        btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_ok = (TextView) view.findViewById(R.id.btn_ok);
        now = (TextView) view.findViewById(R.id.now);

        qiangzhi = (AutoRelativeLayout) view.findViewById(R.id.qiangzhi);
        no_qiangzhi = (AutoLinearLayout) view.findViewById(R.id.no_qiangzhi);


        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.80), LayoutParams.WRAP_CONTENT));

        return this;
    }

    public MyUpdateDialog setTitle(String title) {

        txt_title.setText(title);
        return this;
    }


    public MyUpdateDialog setMsg(String msg) {

        txt_msg.setText(msg);
        return this;
    }


    public MyUpdateDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public MyUpdateDialog setPositiveButton(boolean tag, final OnClickListener listener) {
        if (tag) {
            btn_ok.setText("立即安装");
        } else {
            btn_ok.setText("立即下载");
        }
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
        return this;
    }

    public MyUpdateDialog setNegativeButton(final OnClickListener listener) {
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public MyUpdateDialog setNowButton(Boolean tag, final OnClickListener listener) {
        if (tag) {
            now.setText("立即安装");
        } else {
            now.setText("立即下载");
        }
        now.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
        return this;
    }

    public void setNowVisiable(int isVisiable) {
        now.setVisibility(isVisiable);
    }

    public void setMustVisiable(int isVisiable) {
        qiangzhi.setVisibility(isVisiable);
    }

    public void setNoMustVisiable(int isVisiable) {
        no_qiangzhi.setVisibility(isVisiable);
    }

    public MyUpdateDialog setProgress(String progress) {

        now.setText(progress);
        return this;
    }

    public void dismiss() {
        dialog.dismiss();
    }


    public void show() {
        dialog.show();
    }

    public void setNegativeVisiable(int isVisiable) {
        no_qiangzhi.setVisibility(isVisiable);
    }
    public void setEnable(boolean flag){
        now.setEnabled(flag);
    }
    public void setOnTouchOutside(boolean tag){
        dialog.setCanceledOnTouchOutside(tag);
    }


}
