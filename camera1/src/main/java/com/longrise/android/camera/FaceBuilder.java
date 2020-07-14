package com.longrise.android.camera;

import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.longrise.android.camera.preview.CameraParams;
import com.longrise.android.camera.preview.JpegCallback;
import com.longrise.android.camera.preview.Status;
import com.longrise.android.camera.preview.ParamsCallback;
import com.longrise.android.camera.preview.PreviewStatusListener;


/**
 * Created by godliness on 2020-07-04.
 *
 * @author godliness
 */
public final class FaceBuilder {

    Camera.ShutterCallback mShutterCallback;
    Camera.PictureCallback mRawCallback;
    JpegCallback mJpegCallback;
    PreviewStatusListener mStatusCallback;
    Camera.PreviewCallback mPreviewCallback;
    boolean mTranslucentStatus;

    private final AppCompatActivity mHost;
    private ParamsCallback mParamsCallback;

    public FaceBuilder(AppCompatActivity host) {
        if (host == null) {
            throw new NullPointerException("host == null");
        }
        this.mHost = host;
    }

    /**
     * 配置相关参数，注意按需配置
     */
    public FaceBuilder params(ParamsCallback params) {
        this.mParamsCallback = params;
        return this;
    }

    /**
     * 配置预览状态监听 {@link Status}
     */
    public FaceBuilder previewStatusCallback(PreviewStatusListener stateCallback) {
        this.mStatusCallback = stateCallback;
        return this;
    }

    /**
     * 配置相机预览数据回调
     */
    public FaceBuilder previewCallback(Camera.PreviewCallback previewCallback) {
        this.mPreviewCallback = previewCallback;
        return this;
    }

    /**
     * 配置拍照回调
     */
    public FaceBuilder pictureCallback(
            Camera.ShutterCallback shutterCallback,
            Camera.PictureCallback rawCallback,
            JpegCallback takeCallback) {
        this.mShutterCallback = shutterCallback;
        this.mRawCallback = rawCallback;
        this.mJpegCallback = takeCallback;
        return this;
    }

    /**
     * 沉浸式状态栏
     */
    public FaceBuilder translucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mTranslucentStatus = true;
        }
        return this;
    }

    /**
     * 提交到目标 contentId
     */
    public PreviewProxy commitAndSaveState(Bundle state, @IdRes int contentId) {
        FaceFragment preview;
        if (state == null) {
            preview = createPreview(contentId);
        } else {
            preview = restorePreview();
        }
        return preview.commit(this);
    }

    private FaceFragment createPreview(int contentId) {
        final FaceFragment previewFragment = FaceFragment.newInstance(getParams());
        getFragmentManager().beginTransaction().replace(contentId, previewFragment, PreviewProxy.KEY).commit();
        return previewFragment;
    }

    private FaceFragment restorePreview() {
        return (FaceFragment) getFragmentManager().findFragmentByTag(PreviewProxy.KEY);
    }

    private CameraParams getParams() {
        CameraParams params = null;
        if (mParamsCallback != null) {
            params = mParamsCallback.params();
        }
        if (params == null) {
            return new CameraParams();
        }
        return params;
    }

    private FragmentManager getFragmentManager() {
        return mHost.getSupportFragmentManager();
    }
}
