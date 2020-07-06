package com.longrise.android.camera.preview;

import android.hardware.Camera;

import com.longrise.android.camera.CameraParams;

/**
 * Created by godliness on 2020-07-01.
 *
 * @author godliness
 */
public final class CameraConfig {

    Camera.ShutterCallback mShutterCallback;
    Camera.PictureCallback mRawCallback;
    Camera.PictureCallback mJpegCallback;

    PreviewStatusListener mStateListener;
    ParamsCallback mParamsListener;

    private CameraParams mParams;

    CameraConfig() {
    }

    /**
     * 配置相机预览参数
     */
    public CameraConfig params(ParamsCallback paramsListener) {
        this.mParamsListener = paramsListener;
        return this;
    }

    /**
     * 拍照回调
     */
    public CameraConfig takePicture(
            Camera.ShutterCallback shutterCallback,
            Camera.PictureCallback rawCallback,
            Camera.PictureCallback jpegCallback) {
        this.mShutterCallback = shutterCallback;
        this.mRawCallback = rawCallback;
        this.mJpegCallback = jpegCallback;
        return this;
    }

    /**
     * 预览状态回调
     */
    public CameraConfig previewStatusListener(PreviewStatusListener stateCallback) {
        this.mStateListener = stateCallback;
        return this;
    }

    int cameraId() {
        return cameraParams().mCameraId;
    }

    int pictureWidth() {
        return cameraParams().mPictureWidth;
    }

    int pictureHeight() {
        return cameraParams().mPictureHeight;
    }

    int imageQuality() {
        return cameraParams().mImageQuality;
    }

    boolean checkTakePicture() {
        return mShutterCallback != null
                || mRawCallback != null
                || mJpegCallback != null;
    }

    private CameraParams cameraParams() {
        if (mParams == null) {
            createParams();
        }
        return mParams;
    }

    private void createParams() {
        if (mParamsListener != null) {
            mParams = mParamsListener.params();
        }
        if (mParams == null) {
            mParams = new CameraParams();
        }
    }
}
