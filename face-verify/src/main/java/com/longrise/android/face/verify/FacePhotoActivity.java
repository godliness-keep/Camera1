package com.longrise.android.face.verify;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.longrise.android.face.PhotoFragment;
import com.longrise.android.face.PhotoProxy;
import com.longrise.android.face.verify.assist.VerifyHelper;
import com.longrise.android.face.verify.verify.PhotoParams;

import java.io.ByteArrayOutputStream;

/**
 * Created by godliness on 2020-08-05.
 *
 * @author godliness
 */
public final class FacePhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private PhotoProxy mProxy;
    private PhotoParams mParams;
    /**
     * 标记当前是否切换过照片
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

        mProxy = new PhotoFragment.Builder(this)
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

        Log.e("faceVerifyOnSuccess: ", "faceVerifyOnSuccess");
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
            initVerifyListener();
        }
    };

    private void initVerifyListener() {
        FaceMatchRegistry.getRegistry().registerUploadListener(new FaceMatchRegistry.FaceUploadListener() {
            @Override
            public void onUploadFacePhoto(String base64, @NonNull final FaceVerifyProxy.FaceUploadCallback callback) {
                final Bitmap currentPhoto = mProxy.getCurrentPhoto();
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                currentPhoto.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                final String currentBase64 = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callback.uploadFaceSuccess("123");
                    }
                }, 2000);
            }
        });

        FaceMatchRegistry.getRegistry().registerMatchListener(new FaceMatchRegistry.FaceMatchListener() {
            @Override
            public void onMatchResult(String id, @NonNull FaceVerifyProxy.FaceMatchCallback callback) {
                // todo 在这里完善查询匹配结果
                callback.faceMatchSuccess("你个小糕子，识别成功了");
            }
        });
    }

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
