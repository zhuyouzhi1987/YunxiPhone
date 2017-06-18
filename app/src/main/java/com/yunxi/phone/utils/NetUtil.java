package com.yunxi.phone.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 类名: NetUtil
 * 描述: 网络工具类，判断网络连接
 * 作者：   hanyu
 * 日期：   2016-07-26
 */
public class NetUtil {
		
	/**
	 * 判断网络连接状态
	 * 
	 * @param context
	 */
	public static boolean hasNetwork(Context context) {
		// 获取网络连接管理器
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 需要权限 android.permission.ACCESS_NETWORK_STATE
		NetworkInfo mNetInfo = connect.getActiveNetworkInfo();
		if (mNetInfo == null || !mNetInfo.isAvailable()) {
			return false;
		}
		// 获取网络连接类型： mNetworkInfo.getType()
		return true;
	}
	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
	 *
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}


	/**
	 * 判断网络是否连接
	 *
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != connectivity) {

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * @return
     */
	public static String GetNetworkType(Context mContext) {
		String strNetworkType = "";
		//获取系统的网络服务
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		//获取当前网络类型，如果为空，返回无网络
		NetworkInfo networkInfo =connManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
		{
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
			{
				strNetworkType = "WIFI";
			}
			else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
			{
				String _strSubTypeName = networkInfo.getSubtypeName();

				L.d("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

				// TD-SCDMA   networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
					case TelephonyManager.NETWORK_TYPE_GPRS:
					case TelephonyManager.NETWORK_TYPE_EDGE:
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_1xRTT:
					case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
						strNetworkType = "2G";
						break;
					case TelephonyManager.NETWORK_TYPE_UMTS:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_HSDPA:
					case TelephonyManager.NETWORK_TYPE_HSUPA:
					case TelephonyManager.NETWORK_TYPE_HSPA:
					case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
					case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
					case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
						strNetworkType = "3G";
						break;
					case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
						strNetworkType = "4G";
						break;
					default:
						// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
						if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))
						{
							strNetworkType = "3G";
						}
						else
						{
							strNetworkType = _strSubTypeName;
						}

						break;
				}

			}
		}
		return strNetworkType;
	}

}
