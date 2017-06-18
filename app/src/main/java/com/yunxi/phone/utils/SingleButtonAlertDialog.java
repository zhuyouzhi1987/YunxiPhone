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


public class SingleButtonAlertDialog {
	private Context context;
	private Dialog dialog;
	private AutoLinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;

	private TextView btn_ok;
	private Display display;


	public SingleButtonAlertDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public SingleButtonAlertDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(R.layout.toast_view_single_alertdialog, null);

		// 获取自定义Dialog布局中的控件
		lLayout_bg = (AutoLinearLayout) view.findViewById(R.id.lLayout_bg);
		txt_title = (TextView) view.findViewById(R.id.txt_title);


		txt_msg = (TextView) view.findViewById(R.id.txt_msg);

		btn_ok = (TextView) view.findViewById(R.id.btn_ok);


		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.AlertDialogStyle);
		dialog.setContentView(view);

		// 调整dialog背景大小
		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.80), LayoutParams.WRAP_CONTENT));

		return this;
	}

	public SingleButtonAlertDialog setTitle(String title) {

		txt_title.setText(title);
		return this;
	}



	public SingleButtonAlertDialog setMsg(String msg) {

			txt_msg.setText(msg);
		return this;
	}


	public SingleButtonAlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public SingleButtonAlertDialog setPositiveButton(String text, final OnClickListener listener) {
		btn_ok.setText(text);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}


	public void dismiss(){
		dialog.dismiss();
	}


	public void show() {
		dialog.show();
	}
}
