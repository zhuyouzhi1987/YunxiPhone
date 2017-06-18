package com.yunxi.phone.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by bond on 2016/12/29.
 */
public class TextUtil {
    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final String getMessageDigest(byte[] paramArrayOfByte) {
        char[] arrayOfChar1 = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            int i = arrayOfByte.length;
            char[] arrayOfChar2 = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) {
                if (j >= i)
                    return new String(arrayOfChar2);
                int m = arrayOfByte[j];
                int n = k + 1;
                arrayOfChar2[k] = arrayOfChar1[(0xF & m >>> 4)];
                k = n + 1;
                arrayOfChar2[n] = arrayOfChar1[(m & 0xF)];
                j++;
            }
        } catch (Exception localException) {
        }
        return null;
    }

    /**
     * 正则表达式判断是否为手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        if(mobiles.length()==11&&mobiles.substring(0,1).equals("1")){
            return true;
        }
        return false;

    }

    /**
     * 正则表达式判断是否为电子邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 异或加密
     *
     * @param str
     * @param code
     * @return
     */
    public static String yihuojiami(String str, String code) {
        try {
            str = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        byte[] data = str.getBytes();
        byte[] keyData = code.getBytes();
        int keyIndex = 0;
        for (int x = 0; x < data.length; x++) {
            data[x] = (byte) (data[x] ^ keyData[keyIndex]);
            keyIndex++;
            if (keyIndex == keyData.length) {
                keyIndex = 0;
            }
        }
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static boolean equals(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equals(str2));
    }

    /**
     * 异或解密
     *
     * @param str
     * @param code
     * @return
     */
    public static String yihuojiemi(String str, String code) {
        String result = "";
        byte[] data = Base64.decode(str.getBytes(), Base64.DEFAULT);
        byte[] keyData = code.getBytes();
        int keyIndex = 0;
        for (int x = 0; x < data.length; x++) {
            data[x] = (byte) (data[x] ^ keyData[keyIndex]);
            keyIndex++;
            if (keyIndex == keyData.length) {
                keyIndex = 0;
            }
        }
        try {
            result = URLDecoder.decode(new String(data), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return result;
    }

    public static String getUserAgent() {
//        return yihuojiami("PFSP" + DateTime.now().toString("yyyy-MM-dd hh:mm:ss"), Const.useragent_key) + System.getProperty("http.agent");
        return System.getProperty("http.agent");
    }

    /**
     * 首字母变小写
     */
    public static String firstCharToLowerCase(String str) {
        Character firstChar = str.charAt(0);
        String tail = str.substring(1);
        str = Character.toLowerCase(firstChar) + tail;
        return str;
    }

    /**
     * 首字母变大写
     */
    public static String firstCharToUpperCase(String str) {
        Character firstChar = str.charAt(0);
        String tail = str.substring(1);
        str = Character.toUpperCase(firstChar) + tail;
        return str;
    }

    /**
     * 字符串为 null 或者为 "" 时返回 true
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim()) ? true : false;
    }

    /**
     * 字符串不为 null 而且不为 "" 时返回 true
     */
    public static boolean notBlank(String str) {
        return str == null || "".equals(str.trim()) ? false : true;
    }

    public static boolean notBlank(String... strings) {
        if (strings == null)
            return false;
        for (String str : strings)
            if (str == null || "".equals(str.trim()))
                return false;
        return true;
    }

    public static boolean notNull(Object... paras) {
        if (paras == null)
            return false;
        for (Object obj : paras)
            if (obj == null)
                return false;
        return true;
    }

    /**
     * 判断是否为手机号
     *
     * @param str
     * @return
     */
    public static boolean isPhoneNumber(String str) {
        if (isBlank(str)) {
            return false;
        }
        if (str.length() != 11) {
            return false;
        }
        if (Pattern.matches("1\\d{10}", str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断密码是否小于6位
     *
     * @param str
     * @return
     */
    public static boolean isPassword(String str) {
        if (isBlank(str)) {
            return false;
        }
        if (str.length() < 6) {
            return false;
        }
        return true;
    }


    /**
     * 过滤str中除了字母和汉字的其他字符
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String regEx = "[^a-zA-Z\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String hideCode(String code) {
        if (code == null) {
            return "";
        } else if (code.length() <= 7) {
            String temp = "";
            temp += code.substring(0, 1);
            for (int i = 0; i < code.length() - 1; i++) {
                temp += "*";
            }
            return temp;
        } else {
            String temp = "";
            temp += code.substring(0, 3);
            for (int i = 0; i < code.length() - 7; i++) {
                temp += "*";
            }
            temp += code.substring(code.length() - 4);
            return temp;
        }
    }

    /**
     * 格式化数字
     *
     * @param num
     * @return
     */
    public static String formatNumber(int num) {
        String result;
        if (num > 10000) {
            result = num / 10000 + "w";
        } else if (num > 1000) {
            result = (num / 1000) * 1000 + "+";
        } else {
            result = num + "";
        }
        return result;
    }

    public static String fotmatNumber2(int num) {
        String result;
        if (num > 10000) {
            result = num / 10000 + "万";
        } else {
            result = num + "";
        }
        return result;
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param time 需要格式化的时间 如"2014-07-14 19:01:45"
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(String time) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            try {
                Date tDate = new Date(Long.parseLong(time) * 1000);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                Date bbeforeYes = new Date(yesterday.getTime() - (tDay * 2));
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日 HH:mm");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(tDate);
                    } else {
//                        if (dTime < tMin) {
//                            display = "刚刚";
//                        } else if (dTime < tHour) {
//                            display = (int) Math.ceil(dTime / tMin) + "分钟前";
//                        } else
                        if (dTime < tDay && tDate.after(yesterday)) {
                            display = "今天 " + new SimpleDateFormat("HH:mm").format(tDate);
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "昨天 " + new SimpleDateFormat("HH:mm").format(tDate);
                        } else if (tDate.after(bbeforeYes) && tDate.before(beforeYes)) {
                            display = "前天 " + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return display;
    }

    /**
     * 提取字符串中的数字
     *
     * @param s
     * @return
     */
    public static String getNumberFormString(String s) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        return m.replaceAll("").trim();
    }

    public static void removeBlank(EditText editText) {
        if (editText.getText().toString().contains(" ")) {
            editText.setText(editText.getText().toString().replace(" ", ""));
        }
        Editable etext = editText.getText();
        Selection.setSelection(etext, etext.length());
    }

    public static String findKeyword(String content, String keyword) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(keyword)) {
            return content;
        }
        return content.replace(keyword, "<h><font color=\"#db2013\">" + keyword + "</font></h>");
    }

    /**
     * 格式化数字变成金钱格式
     *
     * @param num
     * @return
     */
    public static String numToIntOrPrice(double num) {
        if ((int) num == num) {
            return (int) num + "";
        } else {
            DecimalFormat df = new java.text.DecimalFormat("0.00");
            return df.format(num);
        }
    }

    /**
     * 格式化数字变成金钱格式
     *
     * @param num
     * @return
     */
    public static String numToPrice(double num) {
        DecimalFormat df = new java.text.DecimalFormat("0.00");
        return df.format(num);
    }

    /**
     * 获取传入毫秒与当前时间的差（天数）
     *
     * @param date
     * @return
     */
    public static String getNumberOfDay(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date(date);
        String a = sdf.format(d1);
        Date d2 = new Date();
        String b = sdf.format(d2);
        try {
            long m = sdf.parse(a).getTime() - sdf.parse(b).getTime();
            return formatTime(m);
        } catch (ParseException e) {
            return null;
        }
    }

    /*
* 毫秒转化
*/
    public static String formatTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
/*        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;*/
        return day + "";
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 时间戳转换  年月日 字符串
     *
     * @return
     */
    public static String timetostring(Long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String d = format.format(new Date(time*1000));
        return d;
    }

    /**
     * 时间戳转换  年月日 字符串
     *
     * @return
     */
    public static String timetostringInTime(Long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(new Date(time*1000));
        return d;
    }

    /**
     * 时间戳转换 相反 年月日 字符串
     *
     * @return
     */
    public static Long stringtotime(String time) {
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd ");
        Long L_date= Long.valueOf(0);
        try {
            Date date = format.parse(time);
            L_date=date.getTime();
        }catch (Exception e){
        }
        return  L_date;
    }

}
