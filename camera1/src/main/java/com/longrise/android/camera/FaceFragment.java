package com.longrise.android.camera;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.longrise.android.camera.preview.CameraConfig;
import com.longrise.android.camera.preview.CameraParams;
import com.longrise.android.camera.preview.CameraPreview;
import com.longrise.android.camera.preview.ParamsCallback;
import com.longrise.android.camera.utils.StrUtil;
import com.longrise.android.camera.widget.WheelView;

/**
 * Created by godliness on 2020-07-04.
 *
 * @author godliness
 * 面部识别Fragment
 */
public final class FaceFragment extends Fragment implements PreviewProxy, View.OnClickListener {
    private ImageView mIvBack;
    private CameraPreview mPreview;
    private WheelView mWaiting;
    private TextView mTips;
    private Button mTakePicture;

    private CameraParams mParams;
    private FaceBuilder mBuilder;

    private Runnable mTipRunnable;

    @Override
    public void setAutoFocus() {
        // do nothing
    }

    /**
     * 通知面部匹配失败
     */
    @Override
    public void notifyVerifyFailed(String msg) {
        stopWheel();
        setTips(msg, false);
        resetTakePicture(true, R.string.modulecamera_reset_take);
    }

    /**
     * 通知面部匹配成功
     */
    @Override
    public void notifyVerifySuccess(String... msg) {
        stopWheel();
        setTips(StrUtil.arrayToString(msg), true);
        resetTakePicture(false, R.string.modulecamera_string_start_verify);
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
        hideTakePicture1();
        // 状态互斥
        startWheel();
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

        mIvBack = getView().findViewById(R.id.iv_back_face_fragment);
        mIvBack.setOnClickListener(this);
        mPreview = getView().findViewById(R.id.camera_preview);
        mWaiting = getView().findViewById(R.id.wheel_view);
        mTips = getView().findViewById(R.id.tv_result_tips);

        mTakePicture = getView().findViewById(R.id.btn_take_picture);
        mTakePicture.setOnClickListener(this);
        openPreview(mBuilder);
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
        if (v.getId() == R.id.btn_take_picture) {
            final TakeInterceptListener intercept = mBuilder.mInterceptListener;
            if (intercept == null || !intercept.interceptTakePicture()) {
                takePicture();
                setTips(null, false);
            }
        } else if (v.getId() == R.id.iv_back_face_fragment) {
            backIntercept();
        }
    }

    PreviewProxy commit(FaceBuilder builder) {
        this.mBuilder = builder;
        return this;
    }

    private void backIntercept() {
        final BackInterceptListener backIntercept = mBuilder.mBackInterceptListener;
        if (backIntercept == null || !backIntercept.interceptClickBack()) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
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

    private void setTips(String msg, boolean isPassed) {
        if (mTips != null) {
            if (TextUtils.isEmpty(msg)) {
                mTips.setText("");
                mTips.setPadding(0,0,0,0);
                mTips.setCompoundDrawables(null, null, null, null);
            } else {
                removeTips();
                showTips(msg,isPassed);
            }
        }
    }

    private void showTips(String msg, boolean isPassed) {
        mTips.setText(msg);
        Drawable drawable = null;
        if (isPassed) {
            drawable = getContext().getResources().getDrawable(R.drawable.moduleface_icon_ok);
        } else {
            drawable = getContext().getResources().getDrawable(R.drawable.moduleface_icon_fail);
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTips.setCompoundDrawables(drawable, null, null, null);
            mTips.setPadding(5,3,5,3);
            mTips.postDelayed(getTipRunnable(), TIP_TIME_OUT);
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
                    setTips(null, false);
                }
            };
        }
        return mTipRunnable;
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
}
