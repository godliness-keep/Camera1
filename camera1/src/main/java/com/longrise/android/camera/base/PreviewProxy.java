package com.longrise.android.camera.base;

import android.os.Bundle;

/**
 * Created by godliness on 2020-06-26.
 *
 * @author godliness
 */
public interface PreviewProxy {

    /**
     * 保存状态 key
     */
    String KEY = "Preview-Fragment";

    /**
     * 拍照
     */
    void takePicture();

    /**
     * 手动对焦
     */
    void setAutoFocus();

    /**
     * 重新开启预览
     */
    void restartPreview();

    /**
     * 是否支持面部检测
     *
     * @return support or ...
     */
    boolean isSupportFaceDetection();
}

