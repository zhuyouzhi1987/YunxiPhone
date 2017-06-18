package com.yunxi.phone.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunxi.phone.R;
import com.zhy.autolayout.AutoLinearLayout;


/**
 * Created by bond on 16/12/6.
 */
public class ShareDialog {


    private Context context;
    private Dialog dialog;
    private Display display;
    private AutoLinearLayout friend, weixin, qq, zone, copy, erweima, weibo, yingyongbao;
    private OnFriendListener friendListener;
    private OnWeixinListener weixinListener;
    private OnQqListener qqListener;
    private OnZoneListener zoneListener;
    private OnWeiboListener weiboListener;
    private OnYingyongListener yingyongListener;
    private OnErweiListener erweiListener;
    private OnCopyListener copyListenerListener;

    public ShareDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public ShareDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.activity_share_dialog, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());
        TextView hint = (TextView)view.findViewById(R.id.hint);
        ACache mCache = ACache.get(context);
        hint.setText("新用户注册填写你的ID："+ mCache.getAsString("user_id") + "，各得100云朵！");
        friend = (AutoLinearLayout) view.findViewById(R.id.weichat_circle);
        friend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                friendListener.onClick();
                dialog.dismiss();
            }
        });
        weixin = (AutoLinearLayout) view.findViewById(R.id.weichat);
        weixin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                weixinListener.onClick();
                dialog.dismiss();
            }
        });

        qq = (AutoLinearLayout) view.findViewById(R.id.qq);
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qqListener.onClick();
                dialog.dismiss();
            }
        });

        zone = (AutoLinearLayout) view.findViewById(R.id.qq_zone);
        zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoneListener.onClick();
                dialog.dismiss();
            }
        });
        erweima = (AutoLinearLayout) view.findViewById(R.id.erweima);
        erweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erweiListener.onClick();
                dialog.dismiss();
            }
        });
        weibo = (AutoLinearLayout) view.findViewById(R.id.weibo);
        weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weiboListener.onClick();
                dialog.dismiss();
            }
        });
        yingyongbao = (AutoLinearLayout) view.findViewById(R.id.yingyongbao);
        yingyongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yingyongListener.onClick();
                dialog.dismiss();
            }
        });
        copy = (AutoLinearLayout) view.findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyListenerListener.onClick();
                dialog.dismiss();
            }
        });


        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialogWindow.setAttributes(lp);

        return this;
    }


    public ShareDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public ShareDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }


    /**
     * 设置条目布局
     */

    public void show() {
        dialog.show();
    }

    public void addClickFriend(OnFriendListener listener) {
        friendListener = listener;

    }

    public void addClickWeixin(OnWeixinListener listener2) {
        weixinListener = listener2;
    }

    public void addClickQq(OnQqListener listener3) {
        qqListener = listener3;
    }

    public void addClickZone(OnZoneListener listener4) {
        zoneListener = listener4;
    }

    public void addClickWeibo(OnWeiboListener listener5) {
        weiboListener = listener5;
    }

    public void addClickYingyong(OnYingyongListener listener6) {
        yingyongListener = listener6;
    }

    public void addClickErwei(OnErweiListener listener7) {
        erweiListener = listener7;
    }

    public void addClickCopy(OnCopyListener listener8) {
        copyListenerListener = listener8;
    }

    public interface OnFriendListener {
        void onClick();
    }

    public interface OnWeixinListener {
        void onClick();
    }

    public interface OnQqListener {
        void onClick();
    }

    public interface OnZoneListener {
        void onClick();
    }

    public interface OnWeiboListener {
        void onClick();
    }

    public interface OnYingyongListener {
        void onClick();
    }

    public interface OnErweiListener {
        void onClick();
    }

    public interface OnCopyListener {
        void onClick();
    }
}
