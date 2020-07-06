package com.longrise.android.camera.preview;

/**
 * Created by godliness on 2020-06-26.
 *
 * @author godliness
 */
public interface PreviewProxy {

    String KEY = "Preview-Fragment";

    void setAutoFocus();

    /**
     * 重新开启预览
     */
    void restartPreview();

    /**
     * 通知识别失败
     */
    void notifyVerifyFailed(String msg);
}
