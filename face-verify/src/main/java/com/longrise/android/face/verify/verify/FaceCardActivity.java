package com.longrise.android.face.verify.verify;

import android.app.Activity;
import android.content.Intent;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.longrise.android.face.verify.R;
import com.longrise.android.face.verify.common.VerifyConsts;
import com.longrise.android.image.crop.Crop;

/**
 * Created by godliness on 2020-08-05.
 *
 * @author godliness
 */
public final class FaceCardActivity extends AppCompatActivity implements View.OnClickListener {

    private View mBack;

    private ImageView mPhoto;
    private TextView mChangePhoto;
    private TextView mTips;
    private TextView mCommit;

    private CardParams mParams;

    private final int TO_FACE_VERIFY = 0;
    private final int TO_SWITCH_FACE = 1;
    private int mCurrentState = TO_SWITCH_FACE;

    private boolean mSwitched;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getExtraData();
        } else {
            onRestoreState(savedInstanceState);
        }
        setContentView(R.layout.moduleface_activity_face_card);
        initView();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.tv_commit) {
            commitTo();
        } else if (id == R.id.iv_back) {
            setResultToFinish();
        } else if (id == R.id.tv_change_photo) {
            showSwitchDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VerifyConsts.RequestCode.GALLERY:
                final Uri uri = CardHelper.getGalleryOnResult(data);
                if (uri != null) {
                    Crop.of(uri, null).withMaxSize(360, 480).withAspect(3, 4).start(this);
                }
                break;

            case VerifyConsts.RequestCode.TAKE_PICTURE:
                break;

            case VerifyConsts.REQUEST_VERIFY_CODE:
                if (data != null && data.getBooleanExtra(VerifyConsts.RESULT_VERIFY_STATUS, false)) {
                    faceVerifyOnSuccess();
                }
                break;
            default:
                break;
        }
        changePhotoState(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CardParams.EXTRA_CARD_PARAMS, mParams);
    }

    private void updatePhoto(Uri uri) {
        mPhoto.setImageURI(uri);
        switchFaceOnSuccess();
    }

    private void changeCommitState() {
        if (mCurrentState == TO_SWITCH_FACE) {
            mCommit.setText(mParams.mHasCard ? "更换证件照" : "上传证件照");
            changePhotoState(false);
        } else if (mCurrentState == TO_FACE_VERIFY) {
            mCommit.setText("人脸识别");
            changePhotoState(true);
        }
    }

    private void changePhotoState(boolean show) {
        mChangePhoto.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 弹出更换证件照选择框
     */
    private void showSwitchDialog() {
        ActionSheet.create().onActionListener(new ActionSheet.OnActionSheetListener() {
            @Override
            public void onChoose(int action) {
                if (action == ActionSheet.PhotoAction.GALLERY) {
                    CardHelper.startGallery(FaceCardActivity.this);
                } else if (action == ActionSheet.PhotoAction.TAKE_PACTURE) {

                }
            }
        }).show(getSupportFragmentManager(), "action-sheet");
    }

    /**
     * 去人脸识别
     */
    private void toFaceVerify() {

    }

    private void commitTo() {
        if (mCurrentState == TO_SWITCH_FACE) {
            showSwitchDialog();
        } else if (mCurrentState == TO_FACE_VERIFY) {
            toFaceVerify();
        }
    }

    private void switchFaceOnSuccess() {
        mCurrentState = TO_FACE_VERIFY;
        changeCommitState();
    }

    private void faceVerifyOnSuccess() {
        mCurrentState = TO_SWITCH_FACE;
        mParams.mHasCard = true;
        changeCommitState();
        mSwitched = true;
    }

    private void initView() {
        final TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.moduleface_string_title_upload);
        mBack = findViewById(R.id.iv_back);
        mPhoto = findViewById(R.id.iv_photo);
        mChangePhoto = findViewById(R.id.tv_change_photo);
        mTips = findViewById(R.id.tv_tips);
        mCommit = findViewById(R.id.tv_commit);
        regEvent(true);
        changeCommitState();
    }

    private void regEvent(boolean event) {
        mChangePhoto.setOnClickListener(event ? this : null);
        mCommit.setOnClickListener(event ? this : null);
        mBack.setOnClickListener(event ? this : null);
    }

    private void getExtraData() {
        final Intent intent = getIntent();
        mParams = intent.getParcelableExtra(CardParams.EXTRA_CARD_PARAMS);
    }

    private void onRestoreState(Bundle state) {
        mParams = state.getParcelable(CardParams.EXTRA_CARD_PARAMS);
    }

    private void setResultToFinish() {
        final Intent intent = new Intent();
        intent.putExtra(VerifyConsts.RESULT_SWITCH_STATUS, mSwitched);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
