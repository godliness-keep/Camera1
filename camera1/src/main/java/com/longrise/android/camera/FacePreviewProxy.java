package com.longrise.android.camera;

import com.longrise.android.camera.base.PreviewProxy;

/**
 * Created by godliness on 2020-08-05.
 *
 * @author godliness
 */
public interface FacePreviewProxy extends PreviewProxy {

    /**
     * 提示超时时间
     */
    int TIP_TIME_OUT = 3000;

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
