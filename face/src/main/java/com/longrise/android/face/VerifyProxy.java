package com.longrise.android.face;


import com.longrise.android.face.base.IProxy;

/**
 * Created by godliness on 2020-08-09.
 *
 * @author godliness
 */
public interface VerifyProxy extends IProxy {

    /**
     * 提示超时时间
     */
    int TIP_TIME_OUT = 3000;

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
}
