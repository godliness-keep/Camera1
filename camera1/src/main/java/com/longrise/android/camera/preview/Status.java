package com.longrise.android.camera.preview;

/**
 * Created by godliness on 2020-07-02.
 *
 * @author godliness
 * 相机相关状态
 */
public final class Status {

    /**
     * 人脸检测失败
     */
    public static final int MSG_FACE_DETECTION_FAILED = -8;
    /**
     * 需要自动对焦时失败
     */
    public static final int MSG_AUTO_FOCUS_FAILED = -7;
    /**
     * 拍照异常
     */
    public static final int CAMERA_TAKE_PICTURE_FAILED = -6;
    /**
     * 参数配置异常
     */
    public static final int CAMERA_CONFIG_FAILED = -5;
    /**
     * 设置预览失败
     */
    public static final int CAMERA_SET_PREVIEW_FAILED = -3;
    /**
     * 画面预览失败
     */
    public static final int CAMERA_PREVIEW_FAILED = -2;
    /**
     * 相机释放出现异常
     */
    public static final int CAMERA_RELEASE_FAILED = -1;
    /**
     * 相机打开失败
     */
    public static final int CAMERA_OPEN_FAILED = 0;
    /**
     * 开始预览
     */
    public static final int MSG_START_PREVIEW = 1;

}
