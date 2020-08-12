package com.longrise.android.face;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.longrise.android.face.assist.FaceHelper;
import com.longrise.android.face.base.BaseBuilder;
import com.longrise.android.face.base.BaseFragment;
import com.longrise.android.face.base.IProxy;
import com.longrise.android.face.common.FaceConsts;
import com.longrise.android.face.params.PhotoParams;
import com.longrise.android.face.utils.Tips;
import com.longrise.android.face.widget.PhotoView;
import com.longrise.android.image.crop.Crop;

import org.dp.facedetection.Face;
import org.dp.facedetection.FaceDetect;

/**
 * Created by godliness on 2020-08-08.
 *
 * @author godliness
 */
public final class PhotoFragment extends BaseFragment<PhotoFragment.Builder> implements IProxy, View.OnClickListener {

    private static final String TAG = "PhotoFragment";

    private PhotoView mPhoto;
    private View mSwitchPhoto;
    private TextView mCommit;
    private TextView mTips;

    private final int TO_FACE_VERIFY = 0;
    private final int TO_SWITCH_FACE = 1;
    private int mCurrentState = TO_SWITCH_FACE;

    private PhotoParams mParams;
    private OnPhotoChangeListener mPhotoChangeListener;

    public interface OnPhotoChangeListener {

        /**
         * 面部识别
         *
         * @param newBitmap 新的图片
         */
        void toVerify(@NonNull Bitmap newBitmap);
    }

    @Override
    protected int getLayoutResource(Bundle state) {
        if (state == null) {
            getExtraData();
        } else {
            onRestoreState(state);
        }
        return R.layout.moduleface_fragment_photo;
    }

    @Override
    protected void initView() {
        mPhoto = findViewById(R.id.iv_photo);
        mTips = findViewById(R.id.tv_tips);
        mCommit = findViewById(R.id.tv_commit);
        mSwitchPhoto = findViewById(R.id.tv_change_photo);
        changeCommitState();
        bindData();
        regEvent(true);
    }

    @Override
    protected void configBuilder(Builder builder) {
        this.mPhotoChangeListener = builder.mUploadListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FaceConsts.RequestCode.GALLERY:
                handleGallery(data);
                break;

            case FaceConsts.RequestCode.CAMERA:
                handleCamera(resultCode);
                break;

            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.tv_commit) {
            commitToState();
        } else if (id == R.id.iv_photo) {
            FaceHelper.showActionSheet(this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PhotoParams.EXTRA_CARD_PARAMS, mParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        regEvent(false);
    }

    private void bindData() {
        if (mParams.hasCard()) {
            Glide.with(this).load(mParams.photoUrl()).error(R.drawable.moduleface_iv_default_face_card).into(mPhoto);
        }
        mTips.setText(mParams.tips());
    }

    private void updatePhoto(Bitmap resource) {
        Tips.log(TAG, "width: " + resource.getWidth() + " height: " + resource.getHeight());
        mPhoto.setImageBitmap(resource);
        mSwitchPhoto.setVisibility(View.VISIBLE);
        switchFaceOnSuccess();
    }

    private void handleCrop(int resultCode, Intent result) {
        final Uri cropUri = FaceHelper.getCropResult(resultCode, result);
        if (cropUri == null) {
            Tips.showTips(this, R.string.moduleface_string_not_found_crop);
            return;
        }

        final Bitmap face = FaceHelper.faceUriToBitmap(cropUri);
        FaceHelper.findFaces(new FaceDetect.Finder(face) {
            @Override
            public void detected(Face[] faces, boolean hasFace) {
                if (!hasFace) {
                    Tips.showTips(PhotoFragment.this, R.string.moduleface_string_not_detect_face);
                } else {
                    updatePhoto(FaceHelper.get2InchesPhoto(face));
                }
            }
        });
    }

    private void handleCamera(int resultCode) {
        final Uri cameraUri = FaceHelper.getCameraOnResult(getActivity(), resultCode);
        if (cameraUri == null) {
            Tips.showTips(this, R.string.moduleface_string_take_picture_not_found);
            return;
        }
        FaceHelper.startCrop(this, cameraUri);
    }

    private void handleGallery(Intent result) {
        final Uri galleryUri = FaceHelper.getGalleryOnResult(result);
        if (galleryUri == null) {
            Tips.showTips(this, R.string.moduleface_string_gallery_not_found);
            return;
        }
        FaceHelper.startCrop(this, galleryUri);
    }

    private void changeCommitState() {
        if (mCurrentState == TO_SWITCH_FACE) {
            mCommit.setText(mParams.hasCard() ? getString(R.string.moduleface_string_change_photo) : getString(R.string.moduleface_string_upload_photo));
        } else if (mCurrentState == TO_FACE_VERIFY) {
            mCommit.setText(getString(R.string.moduleface_string_face_verify));
            showSwitchPhotoState();
        }
    }

    private void showSwitchPhotoState() {
        mSwitchPhoto.setVisibility(View.VISIBLE);
        mPhoto.setOnClickListener(this);
    }

    private void commitToState() {
        if (mCurrentState == TO_SWITCH_FACE) {
            FaceHelper.showActionSheet(this);
        } else if (mCurrentState == TO_FACE_VERIFY) {
            if (mPhotoChangeListener != null) {
                mPhotoChangeListener.toVerify(mPhoto.getImageBitmap());
            }
        }
    }

    private void switchFaceOnSuccess() {
        mCurrentState = TO_FACE_VERIFY;
        changeCommitState();
    }

    private void regEvent(boolean event) {
        mCommit.setOnClickListener(event ? this : null);
    }

    private void getExtraData() {
        final Bundle extra = getArguments();
        if (extra != null) {
            mParams = extra.getParcelable(PhotoParams.EXTRA_CARD_PARAMS);
        }
    }

    private void onRestoreState(Bundle state) {
        mParams = state.getParcelable(PhotoParams.EXTRA_CARD_PARAMS);
    }

    public static final class Builder extends BaseBuilder<IProxy> {

        OnPhotoChangeListener mUploadListener;
        private String mPhotoUrl;
        private String mTips;

        public Builder(AppCompatActivity host) {
            super(host);
        }

        public Builder photoUrl(String url) {
            this.mPhotoUrl = url;
            return this;
        }

        public Builder tips(String tips) {
            this.mTips = tips;
            return this;
        }

        public Builder onPhotoChangeListener(OnPhotoChangeListener uploadListener) {
            this.mUploadListener = uploadListener;
            return this;
        }

        @Override
        protected Fragment createPreview() {
            return new PhotoFragment();
        }

        @Override
        protected Bundle getExtra() {
            final PhotoParams params = new PhotoParams();
            params.photoUrl(mPhotoUrl);
            params.tips(mTips);

            final Bundle extra = new Bundle();
            extra.putParcelable(PhotoParams.EXTRA_CARD_PARAMS, params);
            return extra;
        }
    }
}
