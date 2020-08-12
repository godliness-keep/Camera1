package com.longrise.android.face.verify;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.longrise.android.face.PhotoFragment;
import com.longrise.android.face.verify.assist.VerifyHelper;
import com.longrise.android.face.verify.verify.PhotoParams;

/**
 * Created by godliness on 2020-08-05.
 *
 * @author godliness
 */
public final class FacePhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FacePhotoActivity";

    private PhotoParams mParams;
    /**
     * 标记当前是否切换过图片
     */
    private boolean mChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getExtraData();
        } else {
            onRestoreState(savedInstanceState);
        }
        setContentView(R.layout.moduleface_activity_face_photo);
        initView();

        new PhotoFragment.Builder(this)
                .photoUrl(mParams.mCardUrl)
                .tips(mParams.mTips)
                .onPhotoChangeListener(mPhotoChangeListener)
                .commitAndSaveState(savedInstanceState, R.id.photo_content);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            setResultToFinish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VerifyHelper.RequestCode.VERIFY) {
            if (VerifyHelper.getVerifyResult(resultCode)) {
                faceVerifyOnSuccess();
            }
        }
    }

    private void faceVerifyOnSuccess() {
        this.mChanged = true;
        Log.e(TAG, "faceVerifyOnSuccess");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PhotoParams.EXTRA_CARD_PARAMS, mParams);
    }

    private final PhotoFragment.OnPhotoChangeListener mPhotoChangeListener = new PhotoFragment.OnPhotoChangeListener() {
        @Override
        public void toVerify(@NonNull Bitmap src) {
            FaceVerifyActivity.openFaceVerify(FacePhotoActivity.this);
        }
    };

    private void initView() {
        final TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.moduleface_string_title_upload);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    private void getExtraData() {
        final Intent intent = getIntent();
        mParams = intent.getParcelableExtra(PhotoParams.EXTRA_CARD_PARAMS);
    }

    private void onRestoreState(Bundle state) {
        mParams = state.getParcelable(PhotoParams.EXTRA_CARD_PARAMS);
    }

    private void setResultToFinish() {
        final Intent intent = new Intent();
        if (mChanged) {
            intent.putExtra(VerifyHelper.Extra.PHOTO_URL, "新的图片地址");
        }
        setResult(mChanged ? Activity.RESULT_OK : Activity.RESULT_CANCELED, intent);
        finish();
    }
}
