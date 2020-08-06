package com.longrise.android.camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longrise.android.camera.base.BaseFragment;
import com.longrise.android.camera.preview.CameraPreview;
import com.longrise.android.camera.utils.DpUtil;
import com.longrise.android.camera.utils.StrUtil;
import com.longrise.android.camera.widget.WheelView;

/**
 * Created by godliness on 2020-07-04.
 *
 * @author godliness
 * 面部识别Fragment
 */
public final class FaceFragment extends BaseFragment<FaceBuilder> implements FacePreviewProxy, View.OnClickListener {

    private CameraPreview mPreview;
    private WheelView mWaiting;
    private TextView mTips;
    private TextView mTakePicture;

    private Runnable mTipRunnable;
    private FaceBuilder mBuilder;

    /**
     * 通知面部匹配失败
     */
    @Override
    public void notifyVerifyFailed(String msg) {
        stopWheel();
        setTips(msg);
        resetTakePicture(true, R.string.modulecamera_reset_take);
    }

    /**
     * 通知面部匹配成功
     */
    @Override
    public void notifyVerifySuccess(String... msg) {
        stopWheel();
        setTips(StrUtil.arrayToString(msg));
        resetTakePicture(false, R.string.modulecamera_string_start_verify);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.modulecamera_fragment_face, container, false);
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
    protected void configBuilder(FaceBuilder builder) {
        this.mBuilder = builder;
        if (builder.mTranslucentStatus) {
            adjustTipsLocation();
        }
    }

    @Override
    protected CameraPreview preview() {
        return mPreview;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_take_picture) {
            final FaceInterceptListener intercept = mBuilder.mInterceptListener;
            if (intercept == null || !intercept.interceptTakePicture()) {
                takePicture();
                setTips(null);
            }
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
