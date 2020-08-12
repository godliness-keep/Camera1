package com.longrise.android.face;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.longrise.android.camera.CameraConfig;
import com.longrise.android.camera.CameraPreview;
import com.longrise.android.camera.JpegCallback;
import com.longrise.android.camera.ParamsCallback;
import com.longrise.android.camera.PreviewFrameCallback;
import com.longrise.android.camera.PreviewStatusListener;
import com.longrise.android.face.base.BaseBuilder;
import com.longrise.android.face.base.BaseFragment;
import com.longrise.android.face.listeners.FaceInterceptListener;
import com.longrise.android.face.utils.DpUtil;
import com.longrise.android.face.utils.Tips;
import com.longrise.android.face.widget.WheelView;

/**
 * Created by godliness on 2020-08-08.
 *
 * @author godliness
 */
public final class VerifyFragment extends BaseFragment<VerifyFragment.Builder> implements View.OnClickListener, VerifyProxy {

    private CameraPreview mPreview;
    private WheelView mWaiting;
    private TextView mTips;
    private TextView mTakePicture;

    private Runnable mTipRunnable;
    private Builder mBuilder;

    @Override
    public void setAutoFocus() {

    }

    @Override
    public void restartPreview() {
        if (mPreview != null) {
            mPreview.restartPreview();
        }
    }

    /**
     * 是否支持面部检测
     *
     * @return support or ...
     */
    @Override
    public boolean isSupportFaceDetection() {
        return mPreview != null && mPreview.isSupportFaceDetection();
    }

    /**
     * 通知面部匹配失败
     */
    @Override
    public void notifyVerifyFailed(String msg) {
        stopWheel();
        setTips(msg);
        resetTakePicture(true, R.string.moduleface_string_reset_take);
    }

    /**
     * 通知面部匹配成功
     */
    @Override
    public void notifyVerifySuccess(String... msg) {
        stopWheel();
        setTips(Tips.arrayToString(msg));
        resetTakePicture(false, R.string.moduleface_string_start_verify);
    }

    /**
     * 隐藏拍照
     */
    @Override
    public void hideTakePicture() {
        hideTakePicture1();
        // 状态互斥
        startWheel();
    }

    @Override
    public void takePicture() {
        startWheel();
        if (mPreview != null) {
            mPreview.takePicture();
        }
    }

    @Override
    protected int getLayoutResource(Bundle state) {
        return R.layout.moduleface_fragment_verify;
    }

    @Override
    protected void initView() {
        mPreview = findViewById(R.id.camera_preview);
        mWaiting = findViewById(R.id.wheel_view);
        mTips = findViewById(R.id.tv_verify_tips);
        mTakePicture = findViewById(R.id.tv_take_picture);
        mTakePicture.setOnClickListener(this);
    }

    @Override
    protected void configBuilder(Builder builder) {
        this.mBuilder = builder;
        if (builder.mTranslucentStatus) {
            adjustTipsLocation();
        }
        configPreview(builder);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_take_picture) {
            final FaceInterceptListener intercept = mBuilder.mInterceptListener;
            if (intercept == null || !intercept.interceptTakePicture()) {
                setTips(null);
                takePicture();
            }
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

    private void configPreview(Builder builder) {
        final CameraConfig config = mPreview.openPreview();
        config.previewStatusListener(builder.mStatusListener);
        config.takePicture(builder.mShutterCallback, builder.mRawCallback, builder.mTakeCallback);
        config.previewCallback(builder.mFrameCallback);
        config.faceDetectionListener(builder.mFaceDetectionCallback);
        config.params(builder.mParamsCallback);
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

    private void setTips(String msg) {
        if (mTips != null) {
            if (TextUtils.isEmpty(msg)) {
                mTips.setText("");
                mTips.setBackground(null);
            } else {
                removeTips();
                mTips.setText(msg);
                mTips.setBackground(getResources().getDrawable(R.drawable.moduleface_shape_verify_fail));
                mTips.postDelayed(getTipRunnable(), VerifyProxy.TIP_TIME_OUT);
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

    private void resetTakePicture(boolean show, @StringRes int resid) {
        if (mTakePicture != null) {
            mTakePicture.setVisibility(show ? View.VISIBLE : View.GONE);
            mTakePicture.setText(resid);
        }
    }

    private void hideTakePicture1() {
        if (mTakePicture != null) {
            mTakePicture.setVisibility(View.GONE);
        }
    }

    public static final class Builder extends BaseBuilder<VerifyProxy> {

        boolean mTranslucentStatus;
        Camera.ShutterCallback mShutterCallback;
        Camera.PictureCallback mRawCallback;
        JpegCallback mTakeCallback;
        Camera.FaceDetectionListener mFaceDetectionCallback;

        FaceInterceptListener mInterceptListener;
        ParamsCallback mParamsCallback;
        PreviewStatusListener mStatusListener;
        PreviewFrameCallback mFrameCallback;

        public Builder(AppCompatActivity host) {
            super(host);
        }

        public Builder pictureCallback(Camera.ShutterCallback shutterCallback,
                                       Camera.PictureCallback rawCallback,
                                       JpegCallback takeCallback) {
            this.mShutterCallback = shutterCallback;
            this.mRawCallback = rawCallback;
            this.mTakeCallback = takeCallback;
            return this;
        }

        public Builder params(ParamsCallback paramsCallback) {
            this.mParamsCallback = paramsCallback;
            return this;
        }

        public Builder faceDetectionListener(Camera.FaceDetectionListener detectionListener) {
            this.mFaceDetectionCallback = detectionListener;
            return this;
        }

        public Builder previewStatusCallback(PreviewStatusListener statusListener) {
            this.mStatusListener = statusListener;
            return this;
        }

        public Builder previewFrameListener(PreviewFrameCallback frameCallback) {
            this.mFrameCallback = frameCallback;
            return this;
        }

        public Builder takeInterceptListener(FaceInterceptListener interceptListener) {
            this.mInterceptListener = interceptListener;
            return this;
        }

        public Builder translucentStatus() {
            this.mTranslucentStatus = true;
            return this;
        }

        @Override
        protected Fragment createPreview() {
            return new VerifyFragment();
        }
    }
}
