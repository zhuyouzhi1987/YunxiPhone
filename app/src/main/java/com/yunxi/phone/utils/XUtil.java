package com.yunxi.phone.utils;

import android.content.Context;
import android.os.Build;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bond on 2016/12/29.
 */

public class XUtil {
    /**
     * 发送get请求
     *
     * @param <T>
     */
    public static <T> Callback.Cancelable Get(String url, Map<String, String> map, Callback.CommonCallback<T> callback) {
        RequestParams params = new RequestParams(url);

        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
        }

        Callback.Cancelable cancelable = x.http().get(params, callback);
        return cancelable;
    }

    /**
     * 发送post请求
     *
     * @param <T>
     */
    public static <T> Callback.Cancelable Post(Context cxt, String url, Map<String, Object> map, Callback.CommonCallback<T> callback) {
        RequestParams params = new RequestParams(url);

        StringBuffer sBuffer = new StringBuffer();


        if (map == null) {
            map = new HashMap<>();
        }

        //添加公共参数
        map.put("imei",UserInfoUtil.getIMEI(cxt));
        map.put("mac", UserInfoUtil.getMacAddress(cxt));
        map.put("version_app", UserInfoUtil.getAppVersionName(cxt));
        map.put("version_system", Build.VERSION.RELEASE);
        map.put("channel",UserInfoUtil.getAppMetaData(cxt, "UMENG_CHANNEL"));
        map.put("timestamp", UserInfoUtil.getTimeStamp());

//        if (null != map) {
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                params.addParameter(entry.getKey(), entry.getValue());
//            }
//        }
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            sBuffer.append("&" + key + "=" + map.get(key));
        }
        L.d("====原始串===="+sBuffer.toString().substring(1));


        String Auth = null;
        try {
            Auth = DES_Utils.toHexString(DES_Utils.encrypt(sBuffer.toString().substring(1), AddressApi.DES_PASSWORD));
        } catch (Exception e) {
            e.printStackTrace();
        }

        L.d("====加密串====" + Auth);
        params.addParameter("info", Auth);

        params.setConnectTimeout(5000);
        Callback.Cancelable cancelable = x.http().post(params, callback);
        return cancelable;
    }




    /**
     * 发送首页post请求
     *
     * @param <T>
     */
    public static <T> Callback.Cancelable Post2(String url, Map<String, Object> map, Callback.CommonCallback<T> callback) {
        RequestParams params = new RequestParams(url);

        StringBuffer sBuffer = new StringBuffer();

//        if (null != map) {
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                params.addParameter(entry.getKey(), entry.getValue());
//            }
//        }

        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            sBuffer.append("&" + key + "=" + map.get(key));
        }
        String Auth = null;
        try {
            Auth = DES_Utils.toHexString(DES_Utils.encrypt(sBuffer.toString().substring(1), AddressApi.DES_PASSWORD));

        } catch (Exception e) {
            e.printStackTrace();
        }
        params.addParameter("info", Auth);
        params.setConnectTimeout(5000);

        Callback.Cancelable cancelable = x.http().post(params, callback);
        return cancelable;
    }


    /**
     * 发送post请求，（供目前测试接口使用）
     *
     * @param <T>
     */
    public static <T> Callback.Cancelable Post3(String url, Map<String, Object> map, Callback.CommonCallback<T> callback) {
        RequestParams params = new RequestParams(url);

        if (null != map) {

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }

        Callback.Cancelable cancelable = x.http().post(params, callback);
        return cancelable;
    }


    /**
     * 上传文件
     *
     * @param <T>
     */
    public static <T> Callback.Cancelable UpLoadFile(Context cxt,String url, Map<String, Object> map,List<File> fileList ,Boolean flag,Callback.CommonCallback<T> callback) {
        RequestParams params = new RequestParams(url);
        StringBuffer sBuffer = new StringBuffer();
        if (map == null) {
            map = new HashMap<>();
        }
        //添加公共参数
        map.put("imei",UserInfoUtil.getIMEI(cxt));
        map.put("mac", UserInfoUtil.getMacAddress(cxt));
        map.put("version_app", UserInfoUtil.getAppVersionName(cxt));
        map.put("version_system", Build.VERSION.RELEASE);
        map.put("channel",UserInfoUtil.getAppMetaData(cxt, "UMENG_CHANNEL"));
        map.put("timestamp", UserInfoUtil.getDateToString(System.currentTimeMillis()));

        L.d("时间戳为========"+UserInfoUtil.getDateToString(System.currentTimeMillis()));

        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            sBuffer.append("&" + key + "=" + map.get(key));
        }
        L.d("====原始串===="+sBuffer.toString().substring(1));
        String Auth = null;
        try {
            Auth = DES_Utils.toHexString(DES_Utils.encrypt(sBuffer.toString().substring(1), AddressApi.DES_PASSWORD));

        } catch (Exception e) {
            e.printStackTrace();
        }
        L.d("====加密串====" + Auth);
        params.setMultipart(true);
        params.addParameter("info", Auth);
        if(flag){
            for (File file : fileList) {
                params.addBodyParameter("file", file);
            }
        }
        Callback.Cancelable cancelable = x.http().post(params, callback);
        return cancelable;
    }

    /**
     * 下载文件
     *
     * @param <T>
     */
    public static <T> Callback.Cancelable DownLoadFile(String url, String filepath, Callback.ProgressCallback<T> callback) {
        RequestParams params = new RequestParams(url);
        //设置断点续传
        params.setAutoResume(true);
        params.setSaveFilePath(filepath);
        Callback.Cancelable cancelable = x.http().get(params, callback);
        return cancelable;
    }
}


