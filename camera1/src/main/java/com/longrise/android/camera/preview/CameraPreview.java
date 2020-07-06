package com.longrise.android.camera.preview;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.longrise.android.camera.BuildConfig;

import java.util.List;

/**
 * Created by godliness on 2020-07-01.
 *
 * @author godliness
 */
public final class CameraPreview extends SurfaceView implements Handler.Callback, SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";

    private Camera mCamera;
    private Handler mHandler;

    private CameraConfig mConfig;
    private int mOrientation;

    private Camera.AutoFocusCallback mAutoFocusCallback;

    /**
     * 开启预览
     */
    public CameraConfig openPreview() {
        if (mConfig == null) {
            mConfig = new CameraConfig();
        }
        return mConfig;
    }

    /**
     * 拍照
     */
    public void takePicture() {
        if (mCamera != null) {
            final CameraConfig config = mConfig;
            if (config.checkTakePicture()) {
                mCamera.autoFocus(getAutoFocusCallback());
            }
        }
    }

    /**
     * 重新预览
     */
    public void restartPreview() {
        startPreview();
    }

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("The context must be Activity");
        }
        this.mHandler = new Handler(this);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        createIfCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        destroyCamera();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Status.MSG_START_PREVIEW:
                handleStartPreview();
            default:
                handleError(msg.what, msg.obj);
                break;
        }
        return false;
    }

    private void configPreview() {
        bindingDisplayForCamera();
        configCameraParameters();
    }

    private void configCameraParameters() {
        if (mCamera != null) {
            final Camera.Parameters parameters = configBasicParamseters();
            try {
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                notifyStatusToUser(Status.CAMERA_CONFIG_FAILED, e);
            }
        }
    }

    private void bindingDisplayForCamera() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(getHolder());
            } catch (Exception e) {
                notifyStatusToUser(Status.CAMERA_PREVIEW_FAILED, e);
            }
        }
    }

    private void createIfCamera() {
        if (mCamera == null) {
            try {
                mCamera = CameraProxy.createCamera(mConfig.cameraId());
            } catch (RuntimeException e) {
                notifyStatusToUser(Status.CAMERA_OPEN_FAILED, e);
            }

            if (mCamera != null) {
                configPreview();
            }
        }
    }

    private void startPreview() {
        if (mCamera != null) {
            notifyStatusToUser(Status.MSG_START_PREVIEW);
        }
    }

    private void destroyCamera() {
        if (mCamera != null) {
            try {
                releaseCamera();
            } catch (Exception e) {
                notifyStatusToUser(Status.CAMERA_RELEASE_FAILED, e);
            }
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void handleStartPreview() {
        if (mCamera != null) {
            try {
                mCamera.setDisplayOrientation(mOrientation);
                mCamera.startPreview();
            } catch (Exception e) {
                notifyStatusToUser(Status.CAMERA_PREVIEW_FAILED, e);
            }
        }
    }

    private void releaseCamera() {
        CameraProxy.releaseCamera(mCamera);
        mCamera = null;
    }

    private void handleError(int what, Object ex) {
        if (ex instanceof Exception) {
            if (BuildConfig.DEBUG) {
                ((Exception) ex).printStackTrace();
            }
            final PreviewStatusListener stateListener = mConfig.mStateListener;
            if (stateListener != null) {
                stateListener.onPreviewStatus(what, ((Exception) ex).getMessage());
            }
        }
    }

    private void notifyStatusToUser(int what, Object... params) {
        final Message toUser = mHandler.obtainMessage(what);
        toUser.obj = params;
        toUser.sendToTarget();
    }

    private Camera.AutoFocusCallback getAutoFocusCallback() {
        if (mAutoFocusCallback == null) {
            mAutoFocusCallback = new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        takePictureOnAutoFocus();
                    } else {
                        notifyStatusToUser(Status.MSG_AUTO_FOCUS_FAILED, "Take picture auto focus failed");
                    }
                }
            };
        }
        return mAutoFocusCallback;
    }

    private void takePictureOnAutoFocus() {
        final CameraConfig config = this.mConfig;
        final Camera.ShutterCallback shutterCallback = config.mShutterCallback;
        final Camera.PictureCallback rawCallback = config.mRawCallback;
        final Camera.PictureCallback jpeg = config.mJpegCallback;
        mCamera.takePicture(shutterCallback, rawCallback, jpeg);
    }

    private void configPreviewParameters(Camera.Parameters preview) {
        final int width = getWidth();
        final int height = getHeight();
        final Camera.Size size = CameraProxy.calcOptimaSize(preview.getSupportedPreviewSizes(), width, height);
        preview.setPreviewSize(size.width, size.height);

        printLog("configPreviewParameters width: " + size.width + " height: " + size.height);
    }

    private void configCaptureParameters(Camera.Parameters capture) {
        final CameraConfig config = this.mConfig;
        final int expectWidth = config.pictureWidth();
        final int expectHeight = config.pictureHeight();
        final Camera.Size optimaSize = CameraProxy.calcOptimaSize(capture.getSupportedPictureSizes(), expectWidth, expectHeight);
        capture.setPictureSize(optimaSize.width, optimaSize.height);
        this.mOrientation = CameraProxy.getDisplayOrientation((Activity) getContext(), config.cameraId());
        capture.set("rotation", mOrientation);

        printLog("configCaptureParameters: width: " + optimaSize.width + " height: " + optimaSize.height);
    }

    private Camera.Parameters configBasicParamseters() {
        final CameraConfig config = this.mConfig;
        final Camera.Parameters basic = mCamera.getParameters();
        if (config.mStateListener != null) {
            config.mStateListener.onCameraOpened(basic);
        }
        basic.setPreviewFormat(ImageFormat.NV21);
        basic.setPictureFormat(ImageFormat.JPEG);
        basic.setJpegQuality(config.imageQuality());
        final List<String> supportedFocusModes = basic.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            basic.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        configPreviewParameters(basic);
        configCaptureParameters(basic);
        return basic;
    }

    private void printLog(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
