package com.longrise.android.face;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.longrise.android.face.base.BaseBuilder;
import com.longrise.android.face.base.BaseFragment;
import com.longrise.android.face.params.PhotoParams;
import com.longrise.android.face.params.VerifyParams;
import com.longrise.android.face.widget.RoundImageView;

import org.dp.facedetection.Face;
import org.dp.facedetection.FaceDetect;

/**
 * Created by godliness on 2020-08-09.
 *
 * @author godliness
 */
public final class PreviewFragment extends BaseFragment<PreviewFragment.Builder> implements PreviewProxy, View.OnClickListener {

    private View mVerify;
    private TextView mFaceName;
    private RoundImageView mPhoto;

    private VerifyParams mParams;
    private OnFacePreviewListener mFacePreviewListener;
    private DetectLevel mDetectLevel;

    /**
     * 当前图片是否已经检查过
     */
    private boolean mAlreadyVerify;

    /**
     * 重新预览
     *
     * @param url 新的图片地址
     */
    @Override
    public void restartPreview(String url) {
        if (!isDetached()) {
            mParams.previewUrl(url);
            loadPreviewPhoto(url);
        }
    }

    public interface OnFacePreviewListener {

        /**
         * 人脸检测失败
         */
        void onNotFoundFace();

        /**
         * 图片分辨率过大
         */
        void onBeyondSize(int srcWidth, int srcHeight);

        /**
         * 去人脸识别
         */
        void toVerify();
    }

    @Override
    protected int getLayoutResource(Bundle state) {
        if (state == null) {
            getExtraData();
        } else {
            onRestoreState(state);
        }
        return R.layout.moduleface_fragment_preview;
    }

    @Override
    protected void initView() {
        mVerify = findViewById(R.id.tv_start_verify);
        mPhoto = findViewById(R.id.round_iv_face);
        mPhoto.setBitmapConfig(Bitmap.Config.RGB_565);
        mFaceName = findViewById(R.id.tv_face_name);
        regEvent(true);
        bindData();
    }

    @Override
    protected void configBuilder(Builder builder) {
        this.mFacePreviewListener = builder.mPreviewListener;
        this.mDetectLevel = builder.mDetectLevel;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_start_verify) {
            startFaceDetect();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(VerifyParams.EXTRA_CARD_PARAMS, mParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        regEvent(false);
    }

    private FaceDetect.Finder detectFace(final Bitmap face) {
        return new FaceDetect.Finder(face) {
            @Override
            public void onDetected(Face[] faces, boolean hasFace) {
                if (!hasFace) {
                    faceDetectFailed();
                    return;
                }
                if (mDetectLevel == DetectLevel.ALL) {
                    final int srcWidth = face.getWidth();
                    final int srcHeight = face.getHeight();
                    if (srcWidth > mParams.photoMaxWidth() || srcHeight > mParams.photoMaxHeight()) {
                        onMoreThanSize(srcWidth, srcHeight);
                        return;
                    }
                }
                toVerify();
            }
        };
    }

    private void startFaceDetect() {
        if (mAlreadyVerify) {
            toVerify();
            return;
        }
        if (mDetectLevel == DetectLevel.ALL || mDetectLevel == DetectLevel.FACE) {
            final Bitmap face = mPhoto.getImageBitmap();
            detectFace(face).detect();
        } else {
            toVerify();
        }
    }

    private void toVerify() {
        if (mFacePreviewListener != null) {
            mFacePreviewListener.toVerify();
        }
        mAlreadyVerify = true;
    }

    private void faceDetectFailed() {
        if (mFacePreviewListener != null) {
            mFacePreviewListener.onNotFoundFace();
        }
    }

    private void onMoreThanSize(int srcWidth, int srcHeight) {
        if (mFacePreviewListener != null) {
            mFacePreviewListener.onBeyondSize(srcWidth, srcHeight);
        }
    }

    private void changeVerifyState(int drawable, boolean isClick) {
        if (mVerify != null) {
            mVerify.setBackgroundResource(drawable);
            mVerify.setClickable(isClick);
        }
    }

    private SpannableStringBuilder getStyle(String name) {
        final SpannableStringBuilder style = new SpannableStringBuilder(getString(R.string.moduleface_string_security_verify));
        style.append("\n").append(getString(R.string.moduleface_string_security_prove)).append(name).append(getString(R.string.moduleface_string_security_exam));
        final int nameLength = name.length();
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.moduleface_color_yellow_normal)), 12, 12 + nameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new AbsoluteSizeSpan(18, true), 12, 12 + nameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
    }

    private void regEvent(boolean event) {
        mVerify.setOnClickListener(event ? this : null);
        mVerify.setClickable(false);
    }

    private void bindData() {
        final String name = mParams.name();
        if (TextUtils.isEmpty(name)) {
            mFaceName.setText(String.format("%s\n%s%S", getString(R.string.moduleface_string_security_verify), getString(R.string.moduleface_string_security_prove), getString(R.string.moduleface_string_security_exam)));
        } else {
            mFaceName.setText(getStyle(name));
        }

        final String previewUrl = mParams.previewUrl();
        if (!TextUtils.isEmpty(previewUrl)) {
            loadPreviewPhoto(previewUrl);
        }
    }

    private void loadPreviewPhoto(String url) {
        changeVerifyState(R.drawable.moduleface_layer_list_verify_disable, false);
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (!isDetached()) {
                            mPhoto.setImageBitmap(resource);
                            mAlreadyVerify = false;
                            changeVerifyState(R.drawable.moduleface_selector_start_verify_circle, true);
                        }
                    }
                });
    }

    private void getExtraData() {
        final Bundle extra = getArguments();
        if (extra != null) {
            this.mParams = extra.getParcelable(VerifyParams.EXTRA_CARD_PARAMS);
        }
    }

    private void onRestoreState(Bundle state) {
        mParams = state.getParcelable(PhotoParams.EXTRA_CARD_PARAMS);
    }

    public enum DetectLevel {

        /**
         * 检查是否含有人脸和尺寸
         */
        ALL,
        /**
         * 仅检查人脸
         */
        FACE,
        /**
         * do nothing
         */
        NONE
    }

    public static final class Builder extends BaseBuilder<PreviewProxy> {

        OnFacePreviewListener mPreviewListener;
        DetectLevel mDetectLevel = DetectLevel.ALL;
        private String mName;
        private String mPreviewUrl;

        public Builder(AppCompatActivity host) {
            super(host);
        }

        public Builder name(String name) {
            this.mName = name;
            return this;
        }

        public Builder previewUrl(String url) {
            this.mPreviewUrl = url;
            return this;
        }

        public Builder detectLevel(DetectLevel detectLevel) {
            this.mDetectLevel = detectLevel;
            return this;
        }

        public Builder facePreviewListener(OnFacePreviewListener facePreviewListener) {
            this.mPreviewListener = facePreviewListener;
            return this;
        }

        @Override
        protected Fragment createPreview() {
            return new PreviewFragment();
        }

        @Override
        protected Bundle getExtra() {
            final VerifyParams params = new VerifyParams();
            params.name(mName);
            params.previewUrl(mPreviewUrl);

            final Bundle extra = new Bundle();
            extra.putParcelable(VerifyParams.EXTRA_CARD_PARAMS, params);
            return extra;
        }
    }
}
