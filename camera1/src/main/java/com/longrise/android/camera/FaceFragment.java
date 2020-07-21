package com.longrise.android.camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longrise.android.camera.preview.CameraConfig;
import com.longrise.android.camera.preview.CameraParams;
import com.longrise.android.camera.preview.CameraPreview;
import com.longrise.android.camera.preview.ParamsCallback;
import com.longrise.android.camera.utils.DpUtil;
import com.longrise.android.camera.widget.WheelView;

/**
 * Created by godliness on 2020-07-04.
 *
 * @author godliness
 */
public final class FaceFragment extends Fragment implements PreviewProxy, View.OnClickListener {

    private CameraPreview mPreview;
    private WheelView mWaiting;
    private TextView mTips;
    private TextView mTakePicture;

    private CameraParams mParams;
    private FaceBuilder mBuilder;

    private Runnable mTipRunnable;

    @Override
    public void setAutoFocus() {
    }

    /**
     * 通知面部匹配失败
     */
    @Override
    public void notifyVerifyFailed(String msg) {
        stopWheel();
        setTips(msg);
        resetTakePicture();
    }

    /**
     * 通知面部匹配成功
     */
    @Override
    public void notifyVerifySuccess(String... msg) {
        stopWheel();
        setTips(tipToString(msg));
        hideTakePicture();
    }

    /**
     * 是否支持面部匹配
     */
    @Override
    public boolean isSupportFaceDetection() {
        return mPreview != null && mPreview.isSupportFaceDetection();
    }

    /**
     * 重新预览
     */
    @Override
    public void restartPreview() {
        if (mPreview != null) {
            mPreview.restartPreview();
        }
    }

    /**
     * 隐藏拍照
     */
    @Override
    public void hideTakePicture() {
        if (mTakePicture != null) {
            mTakePicture.setVisibility(View.GONE);
        }
    }

    static FaceFragment newInstance(CameraParams params) {
        final FaceFragment faceFragment = new FaceFragment();
        final Bundle extra = new Bundle();
        extra.putParcelable(CameraParams.EXTRA_PREVIEW_PARAMS, params);
        faceFragment.setArguments(extra);
        return faceFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getExtraData();
        } else {
            onRestoreState(savedInstanceState);
        }
        return inflater.inflate(R.layout.modulecamera_fragment_face, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPreview = getView().findViewById(R.id.camera_preview);
        mWaiting = getView().findViewById(R.id.wheel_view);
        mTips = getView().findViewById(R.id.tv_verify_tips);
        mTakePicture = getView().findViewById(R.id.tv_take_picture);
        mTakePicture.setOnClickListener(this);
        openPreview(mBuilder);
        if (mBuilder.mTranslucentStatus) {
            adjustTipsLocation();
        }
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
    public void onClick(View v) {
        if (v.getId() == R.id.tv_take_picture) {
            takePicture();
        }
    }

    PreviewProxy commit(FaceBuilder builder) {
        this.mBuilder = builder;
        return this;
    }

    private void takePicture() {
        startWheel();
        if (mPreview != null) {
            mPreview.takePicture();
        }
    }

    private void startWheel() {
        if (mWaiting != null) {
            mWaiting.startRotate();
        }
    }

    private void stopWheel() {
        if (mWaiting != null) {
            mWaiting.stopRotate();
        }
    }

    private void openPreview(FaceBuilder builder) {
        final CameraConfig config = mPreview.openPreview();
        config.previewStatusListener(builder.mStatusCallback);
        config.takePicture(builder.mShutterCallback, builder.mRawCallback, builder.mJpegCallback);
        config.previewCallback(builder.mPreviewCallback);
        config.faceDetectionListener(builder.mDetectonListener);
        config.params(new ParamsPackage(mParams));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CameraParams.EXTRA_PREVIEW_PARAMS, mParams);
    }

    private static class ParamsPackage implements ParamsCallback {

        private final CameraParams mParams;

        ParamsPackage(CameraParams params) {
            this.mParams = params;
        }

        @Override
        public CameraParams params() {
            return mParams;
        }
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

    private void setTips(String msg) {
        if (mTips != null) {
            if (TextUtils.isEmpty(msg)) {
                mTips.setText("");
                mTips.setBackground(null);
            } else {
                removeTips();
                mTips.setText(msg);
                mTips.setBackground(getResources().getDrawable(R.drawable.moduleface_shape_verify_fail));
                mTips.postDelayed(getTipRunnable(), TIP_TIME_OUT);
            }
        }
    }

    private void removeTips() {
        if (mTipRunnable != null) {
            mTips.removeCallbacks(mTipRunnable);
        }
    }

    private Runnable getTipRunnable() {
        if (mTipRunnable == null) {
            mTipRunnable = new Runnable() {
                @Override
                public void run() {
                    setTips(null);
                }
            };
        }
        return mTipRunnable;
    }

    private void adjustTipsLocation() {
        final Context cxt = getContext();
        if (cxt != null) {
            final int lr = DpUtil.dip2px(getContext(), 16);
            final int bottom = lr / 2;
            final int topPadding = bottom + DpUtil.getStatusBarHeight(getContext());
            mTips.setPadding(lr, topPadding, lr, bottom);
        }
    }

    private void resetTakePicture() {
        if (mTakePicture != null) {
            mTakePicture.setVisibility(View.VISIBLE);
            mTakePicture.setText(getString(R.string.modulecamera_reset_take));
        }
    }

    private String tipToString(String... msg) {
        final int length;
        if (msg == null || (length = msg.length) <= 0) {
            return null;
        }
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++) {
            b.append(msg[i]);
        }
        return b.toString();
    }
}
