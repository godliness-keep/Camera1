package com.longrise.android.camera.preview;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.longrise.android.camera.BuildConfig;
import com.longrise.android.camera.focus.SensorController;

/**
 * Created by godliness on 2020-07-01.
 *
 * @author godliness
 * https://developer.android.google.cn/guide/topics/media/camera.html#face-detection
 * Github: https://github.com/godliness-keep/Camera1.git
 */
public final class CameraPreview extends SurfaceView implements Handler.Callback, SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";

    private Camera mCamera;
    private Handler mHandler;

    private CameraConfig mConfig;
    private int mOrientation;
    private boolean mSupportFaceDetection;

    private Camera.PictureCallback mJpegCallback;
    private Camera.AutoFocusCallback mAutoFocusCallback;

    /**
     * 自动对焦控制
     */
    @Nullable
    private SensorController mSensorController;
    private SensorController.CameraFocusListener mCameraFocusListener;
    private boolean mSupportConfigFocusMode;

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
        if (mCamera == null) {
            return;
        }
        if (!mConfig.checkTakePicture()) {
            return;
        }
        if (mSensorController != null) {
            mSensorController.lockFocus();
        }
        takePictureOnAutoFocus();
    }

    /**
     * 重新预览
     */
    public void restartPreview() {
        startPreview();
    }

    /**
     * 是否支持人脸数量检测
     */
    public boolean isSupportFaceDetection() {
//        if (mCamera == null) {
//            throw new IllegalStateException("The camera has not been initialized yet");
//        }
        return mSupportFaceDetection;
    }

    /**
     * 开启自动对焦
     */
    public void onStart() {
        if (mSensorController != null) {
            mSensorController.onStart();
        }
        printLog("onStart");
    }

    /**
     * 停止自动对焦
     */
    public void onStop() {
        if (mSensorController != null) {
            mSensorController.onStop();
        }
        printLog("onStop");
    }

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("The context must be Activity");
        }
        initPreview();
    }

    private void initPreview() {
        mHandler = new Handler(this);
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
        if (enableFaceDetection()) {
            stopFaceDetection();
        }
        onStop();
        destroyCamera();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == Status.MSG_START_PREVIEW) {
            handleStartPreview();
        }
        handleError(msg.what, msg.obj);
        return false;
    }

    private void configPreviewParameters() {
        bindingDisplayForCamera();
        configCameraParameters();
    }

    private void configCameraParameters() {
        if (mCamera != null) {
            try {
                mCamera.setParameters(configBasicParamseters(mCamera.getParameters()));
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
                mCamera = CameraProxy.createCamera(params().mCameraId);
            } catch (RuntimeException e) {
                notifyStatusToUser(Status.CAMERA_OPEN_FAILED, e);
            }

            if (mCamera != null) {
                configPreviewParameters();
                // 根据配置创建自动对焦控制器
//                createSensorControllerFromConfig();
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
            // start face detection from user config
            if (enableFaceDetection()) {
                startFaceDetection();
            }

            //After the first turn on, auto focus once
            if (!mSupportConfigFocusMode && params().mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                setAutoFocus();
            }
        }
    }

    private void configCamera(int width, int height) {
        final CameraConfig config = mConfig;
        final PreviewFrameCallback previewCallback = config.mPreviewCallback;
        try {
            if (previewCallback != null) {
                mCamera.setPreviewCallback(new PreviewFrame(width, height, previewCallback));
            }
        } catch (Exception e) {
            notifyStatusToUser(Status.CAMERA_SET_PREVIEW_FAILED, e);
        }

        // set face detection listener
        if (enableFaceDetection()) {
            mCamera.setFaceDetectionListener(config.mFaceDetectionListener);
        }
        printLog("supportFaceDetection: " + mSupportFaceDetection);
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

    private void takePictureOnAutoFocus() {
        final CameraConfig config = this.mConfig;
        final Camera.ShutterCallback shutterCallback = config.mShutterCallback;
        final Camera.PictureCallback rawCallback = config.mRawCallback;
        try {
            mCamera.takePicture(shutterCallback, rawCallback, getJpegCallback());
        } catch (Exception e) {
            notifyStatusToUser(Status.CAMERA_TAKE_PICTURE_FAILED, e);
        }
    }

    private void configPreviewParameters(Camera.Parameters preview) {
        final int width = getWidth();
        final int height = getHeight();
        final Camera.Size size = CameraProxy.calcOptimaSize(preview.getSupportedPreviewSizes(), width, height);
        preview.setPreviewSize(size.width, size.height);
        // preview frame callback
        configCamera(size.width, size.height);

        printLog("configPreviewParameters width: " + size.width + " height: " + size.height);
    }

    private void configCaptureParameters(Camera.Parameters capture) {
        final int expectWidth = params().mPictureWidth;
        final int expectHeight = params().mPictureHeight;
        final Camera.Size optimaSize = CameraProxy.calcOptimaSize(capture.getSupportedPictureSizes(), expectWidth, expectHeight);
        capture.setPictureSize(optimaSize.width, optimaSize.height);
        this.mOrientation = CameraProxy.getDisplayOrientation((Activity) getContext(), params().mCameraId);
        capture.setRotation(mOrientation);

        printLog("configCaptureParameters: width: " + optimaSize.width + " height: " + optimaSize.height);
        printLog("rotation: " + mOrientation);
    }

    private void configFocusMode(Camera.Parameters basic) {
        final String focusMode = params().mFocusMode;
        this.mSupportConfigFocusMode = CameraProxy.isSupportFocusMode(basic, focusMode);
        if (mSupportConfigFocusMode) {
            basic.setFocusMode(focusMode);
        } else {
            printLog("current focus mode: " + basic.getFocusMode());
            // 此时不支持配置的对焦模式
            // 开启传感器控制对焦模式
            createSensorControllerFromConfig();
        }
    }

    private Camera.Parameters configBasicParamseters(Camera.Parameters basic) {
        final CameraConfig config = this.mConfig;
        if (config.mStateListener != null) {
            config.mStateListener.onCameraOpened(basic);
        }

        basic.setPreviewFormat(CameraProxy.getSupportPreviewFormat(basic));
        basic.setPictureFormat(ImageFormat.JPEG);
        basic.setJpegQuality(params().mImageQuality);
        configFocusMode(basic);
        // set preview fps range[min,max]
        final int[] fpsRanges = CameraProxy.getSupportedPreviewFpsRange(basic, params().mMinFps, params().mMaxFps);
        if (fpsRanges != null) {
            basic.setPreviewFpsRange(fpsRanges[0], fpsRanges[1]);
            printLog("optima minFps: " + fpsRanges[0] + " maxFps: " + fpsRanges[1]);
        }
        // is support face detected
        this.mSupportFaceDetection = basic.getMaxNumDetectedFaces() > 0;
        printLog("maxNumDetectedFaces: " + basic.getMaxNumDetectedFaces());

        configPreviewParameters(basic);
        configCaptureParameters(basic);
        return basic;
    }

    private void setAutoFocus() {
        if (mCamera != null) {
            try {
                mCamera.autoFocus(getAutoFocusCallback());
            } catch (Exception e) {
                notifyStatusToUser(Status.MSG_AUTO_FOCUS_FAILED, e);
            }
        }
    }

    private CameraParams params() {
        return mConfig.params();
    }

    /**
     * 根据配置决定是否要创建自动对焦控制器
     */
    private void createSensorControllerFromConfig() {
        if (params().mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            if (mSensorController == null) {
                mSensorController = new SensorController(getContext());
                mSensorController.setCameraFocusListener(getCameraFocusListener());
                mSensorController.onStart();
            } else {
                mSensorController.unlockFocus();
                mSensorController.resetFocus();
            }
        } else {
            if (mSensorController != null) {
                mSensorController.lockFocus();
            }
        }
    }

    private SensorController.CameraFocusListener getCameraFocusListener() {
        if (mCameraFocusListener == null) {
            mCameraFocusListener = new SensorController.CameraFocusListener() {
                @Override
                public void onFocus() {
                    setAutoFocus();
                    printLog("onFocus");
                }
            };
        }
        return mCameraFocusListener;
    }

    private Camera.PictureCallback getJpegCallback() {
        if (mJpegCallback == null) {
            mJpegCallback = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (mSensorController != null) {
                        mSensorController.unlockFocus();
                    }
                    final JpegCallback jpegCallback = mConfig.mJpegCallback;
                    if (jpegCallback != null) {
                        jpegCallback.onJpegTaken(data, camera);
                    }
                }
            };
        }
        return mJpegCallback;
    }

    private Camera.AutoFocusCallback getAutoFocusCallback() {
        if (mAutoFocusCallback == null) {
            mAutoFocusCallback = new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    try {
                        mCamera.cancelAutoFocus();
                    } catch (Exception e) {
                        notifyStatusToUser(Status.MSG_AUTO_FOCUS_FAILED, e);
                    }
                }
            };
        }
        return mAutoFocusCallback;
    }

    private void startFaceDetection() {
        if (enableFaceDetection()) {
            if (mCamera != null) {
                try {
                    mCamera.startFaceDetection();
                } catch (Exception e) {
                    notifyStatusToUser(Status.MSG_FACE_DETECTION_FAILED, e);
                }
                printLog("startFaceDetection");
            }
        }
    }

    private void stopFaceDetection() {
        if (enableFaceDetection()) {
            if (mCamera != null) {
                try {
                    mCamera.stopFaceDetection();
                } catch (Exception e) {
                    notifyStatusToUser(Status.MSG_FACE_DETECTION_FAILED, e);
                }
                printLog("stopFaceDetection");
            }
        }
    }

    private boolean enableFaceDetection() {
        return mSupportFaceDetection && mConfig.mFaceDetectionListener != null;
    }

    private void printLog(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
