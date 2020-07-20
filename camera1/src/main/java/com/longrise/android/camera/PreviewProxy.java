package com.longrise.android.camera;

/**
 * Created by godliness on 2020-06-26.
 *
 * @author godliness
 */
public interface PreviewProxy {

    /**
     * 提示超时时间
     */
    int TIP_TIME_OUT = 3000;
    /**
     * 保存状态key
     */
    String KEY = "Preview-Fragment";

    /**
     * 手动对焦
     */
    void setAutoFocus();

    /**
     * 重新开启预览
     */
    void restartPreview();

    /**
     * 隐藏拍照
     */
    void hideTakePicture();

    /**
     * 通知匹配失败
     */
    void notifyVerifyFailed(String msg);

    /**
     * 通知匹配成功
     */
    void notifyVerifySuccess(String... msg);

    /**
     * 是否支持面部检测
     *
     * @return support or ...
     */
    boolean isSupportFaceDetection();

    /**
     * 开始人脸检测
     */
    void startFaceDetection();

    /**
     * 停止人脸检测
     */
    void stopFaceDetection();
}

