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

/**
 * Created by Administrator on 2017/1/20.
 */
public class SelectDialog {
    private Context context;
    private Dialog dialog;
    private Display display;
    private OnGraphListener graphListener;
    private OnPhotoListener photoListener;
    private OnCancleListener cancleListener;
    TextView canle;
    TextView photo;
    TextView graph;

    public SelectDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public SelectDialog builder() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.activity_select_dialog, null);
        photo = (TextView) view.findViewById(R.id.photo);
        graph = (TextView) view.findViewById(R.id.graph);
        canle = (TextView) view.findViewById(R.id.canle);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphListener.onClick();
                dialog.dismiss();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoListener.onClick();
                dialog.dismiss();
            }
        });
        canle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleListener.onClick();
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


    public SelectDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public SelectDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    /**
     * 设置条目布局
     */

    public void show() {
        dialog.show();
    }

    public void addClickGraph(OnGraphListener listener) {
        graphListener = listener;
    }

    public void addClickPhoto(OnPhotoListener listener) {
        photoListener = listener;
    }

    public void addClickCancle(OnCancleListener listener) {
        cancleListener = listener;
    }

    public interface OnGraphListener {
        void onClick();
    }

    public interface OnPhotoListener {
        void onClick();
    }

    public interface OnCancleListener {
        void onClick();
    }
}
