package com.yunxi.phone.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.yunxi.phone.R;
import com.yunxi.phone.utils.StatusBarUtil;


/**
 * activity基类
 * @author bond
 *
 */
public abstract class BaseActivity extends FragmentActivity {

	public static final String TAG = BaseActivity.class.getSimpleName();

	protected Handler mHandler = null;

	/**
	 * 相当于findviewById方法
	 *
	 * @param resId
	 * @param <T>
	 * @return
	 */
	public <T extends View> T $(int resId) {
		View view = (T) super.findViewById(resId);
		return (T) view;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initData();
	}

	private void initData(){
		AppManager.getInstance().addActivity(this);
		int layout=getLayoutId();
		setContentView(layout);


		/**
		 * 将原生6.0系统的状态栏字体变成深色，暂时关掉。我们的状态栏是绿的的。暂时不需要
		 */
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//		}

//		StatusBarUtil.FlymeSetStatusBarLightMode(getWindow(),true);
//		StatusBarUtil.MIUISetStatusBarLightMode(getWindow(),true);


		setStatusBar();

		findById();
		regListener();
		init();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/**
	 * 加载布局
	 */
	protected abstract int getLayoutId();

	/**
	 * 初始化组
	 */
	protected abstract void findById();

	/**
	 * 注册监听
	 */
	protected abstract void regListener();

	/**
	 * 加载数据
	 */
	protected abstract void init();

	protected void setStatusBar() {
		StatusBarUtil.setColor(this, getResources().getColor(R.color.appMainColor));
	}


	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	/**
	 * 通过类名启动Activity，并且含有Bundle数据
	 * 
	 * @param pClass
	 * @param pBundle
	 */
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过Action启动Activity
	 * 
	 * @param pAction
	 */
	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	/**
	 * 通过Action启动Activity，并且含有Bundle数据
	 * 
	 * @param pAction
	 * @param pBundle
	 */
	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideKeyboard(v, ev)) {
				hideKeyboard(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}


	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
	 *
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideKeyboard(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = {0, 0};
			v.getLocationInWindow(l);
			int left = l[0],
					top = l[1],
					bottom = top + v.getHeight(),
					right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 获取InputMethodManager，隐藏软键盘
	 * @param token
	 */
	private void hideKeyboard(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
