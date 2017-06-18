package com.yunxi.phone.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.yunxi.phone.R;

/**
 * Created by liufeng on 16/8/10.
 */
public class MyShareUtils {
    public static void Share(Activity activity, SHARE_MEDIA share_media, UMShareListener umShareListener,String con,String url,String title,Context mContext) {
        ShareAction shareAction = new ShareAction(activity);
        shareAction.withText(con);
//        shareAction.withMedia(image);
//        shareAction.withMedia(music);
//        shareAction.withMedia(video);
        shareAction.withTitle(title);
        shareAction.withTargetUrl(url);
        shareAction.withMedia(new UMImage(mContext,
                BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.invate_logo)));
        shareAction.setPlatform(share_media).setCallback(umShareListener).share();
    }

    public static void AuthLogin(Activity activity, SHARE_MEDIA share_media, UMAuthListener umAuthListener){
        UMShareAPI mShareAPI = UMShareAPI.get(activity);
        mShareAPI.doOauthVerify(activity, share_media, umAuthListener);
    }
}