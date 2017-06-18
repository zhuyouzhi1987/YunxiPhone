package com.yunxi.phone.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.util.Stack;

/**
 * 应用程序Activity的管理类
 * @author bond
 *
 */
public class AppManager {
	private static Stack<Activity> mActivityStack;
	private static Stack<FragmentActivity> mFragmentActivityStack;
	private static AppManager mAppManager;

	private AppManager() {
	}

	/**
	 * 单一实例
	 */
	public static AppManager getInstance() {
		if (mAppManager == null) {
			mAppManager = new AppManager();
		}
		return mAppManager;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (mActivityStack == null) {
			mActivityStack = new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}
	/**
	 * 添加FragmentActivity到堆栈
	 * @param activity
	 */
	public void addFragmentActivity(FragmentActivity activity){
		if(mFragmentActivityStack==null){
			mFragmentActivityStack=new Stack<FragmentActivity>();
		}
		mFragmentActivityStack.add(activity);
	}

	/**
	 * 获取栈顶Activity（堆栈中最后一个压入的）
	 */
	public Activity getTopActivity() {
		Activity activity = mActivityStack.lastElement();
		return activity;
	}

	/**
	 * 结束栈顶Activity（堆栈中最后一个压入的）
	 */
	public void killTopActivity() {
		Activity activity = mActivityStack.lastElement();
		killActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void killActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void killActivity(Class<?> cls) {
		for (Activity activity : mActivityStack) {
			if (activity.getClass().equals(cls)) {
				killActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void killAllActivity() {
		for (int i = 0, size = mActivityStack.size(); i < size; i++) {
			if (null != mActivityStack.get(i)) {
				mActivityStack.get(i).finish();
			}
		}
		mActivityStack.clear();
	}
	/**
	 * 结束所有的FragmentActivity
	 * @param  activity
	 */
	public void KillFragmentActivity(){
		for (int i = 0, size = mFragmentActivityStack.size(); i < size; i++) {
			if (null != mFragmentActivityStack.get(i)) {
				mFragmentActivityStack.get(i).finish();
			}
		}
		mFragmentActivityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	@SuppressWarnings("deprecation")
	public void AppExit(Context context) {
		try {
			killAllActivity();
			KillFragmentActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}
	}
}
