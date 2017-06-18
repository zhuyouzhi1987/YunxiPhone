package com.yunxi.phone.utils;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Looper;

import java.io.File;

/**
 * 用于保存图片后刷新图片媒体库
 */
public class JDMediaScannerConnectionClient implements MediaScannerConnection
        .MediaScannerConnectionClient {

    private File newFile;
    private MediaScannerConnection mediaScannerConnection;

    public JDMediaScannerConnectionClient(File newFile) {
        this.newFile = newFile;
    }

    public void setMediaScannerConnection(MediaScannerConnection mediaScannerConnection) {
        this.mediaScannerConnection = mediaScannerConnection;
    }

    @Override
    public void onMediaScannerConnected() {
        mediaScannerConnection.scanFile(newFile.getAbsolutePath(),null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        Looper.prepare();
//            ToastUtil.show("保存成功" + " \n相册" + File.separator + FileUtils
//                    .FILE_SAVE + File.separator + newFile.getName());
        Looper.loop();
    }
}