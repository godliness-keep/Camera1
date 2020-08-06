package com.longrise.android.camera.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.longrise.android.camera.preview.CameraConfig;
import com.longrise.android.camera.preview.CameraParams;
import com.longrise.android.camera.preview.CameraPreview;
import com.longrise.android.camera.preview.ParamsCallback;

/**
 * Created by godliness on 2020-08-06.
 *
 * @author godliness
 */
public abstract class BaseFragment<Builder extends BaseBuilder> extends Fragment {

    private CameraPreview mPreview;
    private CameraParams mParams;
    private Builder mBuilder;

    @Override
    public final void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            getExtraData();
        } else {
            onRestoreState(savedInstanceState);
        }
        initView();
        configPreview();
    }

    protected abstract void initView();

    protected abstract CameraPreview preview();

    protected abstract void configBuilder(Builder builder);

    private void configPreview() {
        final Builder builder = this.mBuilder;
        this.mPreview = preview();
        final CameraConfig config = mPreview.openPreview();
        config.previewStatusListener(builder.mStatusCallback);
        config.takePicture(builder.mShutterCallback, builder.mRawCallback, builder.mJpegCallback);
        config.previewCallback(builder.mPreviewCallback);
        config.faceDetectionListener(builder.mDetectonListener);
        config.params(new ParamsCallback() {
            @Override
            public CameraParams params() {
                return mParams;
            }
        });
        configBuilder(builder);
    }

    protected final <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPreview != null) {
            mPreview.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPreview != null) {
            mPreview.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CameraParams.EXTRA_PREVIEW_PARAMS, mParams);
    }

    /**
     * 拍照
     */
    public void takePicture() {
        if (mPreview != null) {
            mPreview.takePicture();
        }
    }

    /**
     * Commit create
     */
    public final PreviewProxy commit(Builder builder) {
        this.mBuilder = builder;
        return (PreviewProxy) this;
    }

    /**
     * 手动对焦
     */
    public final void setAutoFocus() {
        // do nothing
    }

    /**
     * 重新开启预览
     */
    public final void restartPreview() {
        if (mPreview != null) {
            mPreview.restartPreview();
        }
    }

    /**
     * 是否支持面部检测
     *
     * @return support or ...
     */
    public final boolean isSupportFaceDetection() {
        return mPreview != null && mPreview.isSupportFaceDetection();
    }

    private void getExtraData() {
        final Bundle extra = getArguments();
        if (extra != null) {
            this.mParams = extra.getParcelable(CameraParams.EXTRA_PREVIEW_PARAMS);
        }
    }

    private void onRestoreState(Bundle state) {
        this.mParams = state.getParcelable(CameraParams.EXTRA_PREVIEW_PARAMS);
    }

}
