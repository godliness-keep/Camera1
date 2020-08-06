package com.longrise.android.camera.base;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.longrise.android.camera.FaceFragment;
import com.longrise.android.camera.preview.CameraParams;
import com.longrise.android.camera.preview.JpegCallback;
import com.longrise.android.camera.preview.ParamsCallback;
import com.longrise.android.camera.preview.PreviewFrameCallback;
import com.longrise.android.camera.preview.PreviewStatusListener;
import com.longrise.android.camera.preview.Status;

/**
 * Created by godliness on 2020-08-05.
 *
 * @author godliness
 */
@SuppressWarnings("unused")
public abstract class BaseBuilder<T extends BaseBuilder, Proxy extends PreviewProxy> {

    Camera.ShutterCallback mShutterCallback;
    Camera.PictureCallback mRawCallback;
    JpegCallback mJpegCallback;

    PreviewStatusListener mStatusCallback;
    PreviewFrameCallback mPreviewCallback;
    Camera.FaceDetectionListener mDetectonListener;

    private final AppCompatActivity mHost;
    private ParamsCallback mParamsCallback;

    protected BaseBuilder(AppCompatActivity host) {
        if (host == null) {
            throw new NullPointerException("host == null");
        }
        this.mHost = host;
    }

    /**
     * 配置相关参数，注意按需配置
     */
    public T params(ParamsCallback params) {
        this.mParamsCallback = params;
        return current();
    }

    /**
     * 配置预览状态监听 {@link Status}
     */
    public T previewStatusCallback(PreviewStatusListener stateCallback) {
        this.mStatusCallback = stateCallback;
        return current();
    }

    /**
     * 配置相机预览数据回调
     */
    public T previewCallback(PreviewFrameCallback previewCallback) {
        this.mPreviewCallback = previewCallback;
        return current();
    }

    /**
     * 监听人脸检测
     */
    public T faceDetectionListener(Camera.FaceDetectionListener detectionListener) {
        this.mDetectonListener = detectionListener;
        return current();
    }

    /**
     * 配置拍照回调
     */
    public T pictureCallback(
            Camera.ShutterCallback shutterCallback,
            Camera.PictureCallback rawCallback,
            JpegCallback takeCallback) {
        this.mShutterCallback = shutterCallback;
        this.mRawCallback = rawCallback;
        this.mJpegCallback = takeCallback;
        return current();
    }

    @SuppressWarnings("unchecked")
    public Proxy commitAndSaveState(Bundle state, @IdRes int contentId) {
        BaseFragment preview;
        if (state == null) {
            preview = commitPreview(contentId);
        } else {
            preview = restorePreview();
        }
        return (Proxy) preview.commit(this);
    }

    protected abstract BaseFragment createPreview();

    private FaceFragment restorePreview() {
        return (FaceFragment) getFragmentManager().findFragmentByTag(PreviewProxy.KEY);
    }

    private BaseFragment commitPreview(int contentId) {
        final BaseFragment previewProxy = createPreview();
        previewProxy.setArguments(getExtra());
        getFragmentManager().beginTransaction().replace(contentId, previewProxy, PreviewProxy.KEY).commit();
        return previewProxy;
    }

    private Bundle getExtra() {
        final Bundle extra = new Bundle();
        extra.putParcelable(CameraParams.EXTRA_PREVIEW_PARAMS, getParams());
        return extra;
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

    @SuppressWarnings("unchecked")
    private T current() {
        return (T) this;
    }
}
