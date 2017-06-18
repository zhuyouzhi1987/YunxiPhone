package com.yunxi.phone.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.yunxi.phone.bean.RecordBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public class CallRecordUtils {
    /**
     * 利用系统CallLog获取通话历史记录
     *
     * @param activity
     * @return
     */
    public String getDataList(Activity activity) {
        // 1.获得ContentResolver
        ContentResolver resolver = activity.getContentResolver();
        // 2.利用ContentResolver的query方法查询通话记录数据库
        /**
         * @param uri 需要查询的URI，（这个URI是ContentProvider提供的）
         * @param projection 需要查询的字段
         * @param selection sql语句where之后的语句
         * @param selectionArgs ?占位符代表的数据
         * @param sortOrder 排序方式
         *
         */
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                new String[]{CallLog.Calls.CACHED_NAME// 通话记录的联系人
                        , CallLog.Calls.NUMBER// 通话记录的电话号码
                        , CallLog.Calls.DATE// 通话记录的日期
//                        , CallLog.Calls.DURATION// 通话时长
//                        , CallLog.Calls.TYPE
//                        , CallLog.Calls.GEOCODED_LOCATION
                }// 通话类型
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        // 3.通过Cursor获得数据
        List<RecordBean> list = new ArrayList<RecordBean>();
        String date;
        ContactsManager manager = new ContactsManager(activity, activity.getContentResolver());
        while (cursor != null && cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

//            if(isToday(new Date(dateLong))){
//
//                date = new SimpleDateFormat("HH:mm").format(new Date(dateLong));
//            }else{
//                date = new SimpleDateFormat("MM-dd HH:mm").format(new Date(dateLong));
//            }
            date = new SimpleDateFormat("HH:mm").format(new Date(dateLong));
//            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
//            String addr = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));
            String typeString = "";
            RecordBean recor = new RecordBean();
            recor.setName(name);
            recor.setNumber(number.replace(" ", ""));
            recor.setDate(date);
            recor.setDatetime(dateLong);
//            recor.setAddr((TextUtils.isEmpty(addr)) ? "未知归属地" : addr);
            recor.setType(typeString);
            String contactID = manager.getContactID(name);
            recor.setId(contactID);
            if (!contactID.equals("0")) {
                //存在
                String note = manager.getNote(name);
                recor.setIsExite(true);
                recor.setNote(note);
            } else {
                recor.setIsExite(false);
            }

            list.add(recor);
        }

        Log.d("tag", list.toString());
        String jsonList = JSON.toJSONString(list);
//        ACache mCache = ACache.get(activity);
//        mCache.put("record",jsonList);

        return jsonList;
    }

    /**
     * 删除某个记录
     */
    public static boolean deleteCallLog(FragmentActivity activity, String callLog) {
        ContentResolver resolver = activity.getContentResolver();
        int result = resolver.delete(CallLog.Calls.CONTENT_URI, "number=?", new String[]{callLog});
        if (result > 0) {
            //删除成功
            return true;
        } else {
            //删除失败
            return false;
        }
    }

    /**
     * 删除某个记录
     */
    public static boolean deleteCallAll(FragmentActivity activity) {
        ContentResolver resolver = activity.getContentResolver();
        int result = resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
        if (result > 0) {
            //删除成功
            return true;
        } else {
            //删除失败
            return false;
        }
    }

    public static boolean isToday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH) + 1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH) + 1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        if (year1 == year2 && month1 == month2 && day1 == day2) {
            return true;
        }
        return false;
    }

    public void insertCallLog(Context context, String name, String number, long date) {
        try {
            // TODO Auto-generated method stub

        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.CACHED_NAME, name);
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
        values.put(CallLog.Calls.DATE, date);
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DURATION, 1);
        values.put(CallLog.Calls.NEW, "1");
        context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
        } catch (Exception e) {
            // Arbitrary and not worth documenting, as Activity
            // Manager will kill this process shortly anyway.
            L.d(e.toString());
        }

    }
}
