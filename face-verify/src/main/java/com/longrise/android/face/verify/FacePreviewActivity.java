package com.longrise.android.face.verify;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.longrise.android.face.verify.common.VerifyConsts;
import com.longrise.android.face.verify.widget.RoundImageView;

/**
 * Created by godliness on 2020-07-07.
 *
 * @author godliness
 * 面部预览
 */
public final class FacePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CAMERA_PERMISSION = 101;

    private View mBack;
    private View mVerify;
    private TextView mFaceName;
    private RoundImageView mIvFace;

    private String mName;
    private String mPreviewPath;

    /**
     * 开启面部预览
     *
     * @param previewPath 预览的图片地址
     * @param name        名字
     */
    public static void openFacePreview(Activity host, String previewPath, String name) {
        final Intent intent = new Intent(host, FacePreviewActivity.class);
        intent.putExtra(VerifyConsts.EXTRA_PREVIEW_PATH, previewPath);
        intent.putExtra(VerifyConsts.EXTRA_PREVIEW_NAME, name);
        host.startActivityForResult(intent, VerifyConsts.REQUEST_VERIFY_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getExtraData();
        } else {
            onRestoreState();
        }
        setContentView(R.layout.activity_face_preivew);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VerifyConsts.REQUEST_VERIFY_CODE) {
            if (data != null && data.getBooleanExtra(VerifyConsts.RESULT_VERIFY_STATUS, false)) {
                setSuccessToResult();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予
                startVerify();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // 需要给用户进一步解释为什么要使用该权限
                    showRequestPermissionRationale();
                } else {
                    // 此时可以肯定用户已经勾选了不再提示
                    // 提示用户如果想再次申请，只能去系统设置中开启
                    showSystemSetting();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.tv_start_verify) {
            beforeStartVerify();
        } else if (id == R.id.iv_back_face_preview) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        regEvent(false);
        super.onDestroy();
    }

    private void initView() {
        mBack = findViewById(R.id.iv_back_face_preview);
        mVerify = findViewById(R.id.tv_start_verify);
        mIvFace = findViewById(R.id.round_iv_face);
        mFaceName = findViewById(R.id.tv_face_name);
        regEvent(true);
        bindData();
    }

    private void regEvent(boolean event) {
        mVerify.setOnClickListener(event ? this : null);
        mBack.setOnClickListener(event ? this : null);
    }

    private void setSuccessToResult() {
        final Intent intent = new Intent();
        intent.putExtra(VerifyConsts.RESULT_VERIFY_STATUS, true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void startVerify() {
        FaceVerifyActivity.openFaceVerify(this);
    }

    private void bindData() {
        if (TextUtils.isEmpty(mName)) {
            mFaceName.setText(String.format("%s\n%s%S", getString(R.string.moduleverify_string_security_verify), getString(R.string.moduleverify_string_security_prove), getString(R.string.moduleverify_string_security_exam)));
        } else {
            mFaceName.setText(getStyle());
        }

        if (!TextUtils.isEmpty(mPreviewPath)) {
            Glide.with(this).asBitmap().load(mPreviewPath).centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    mIvFace.setImageBitmap(resource);

                    FaceDetector faceDetector = new FaceDetector(resource.getWidth(), resource.getHeight(), 5);
                    FaceDetector.Face[] faces = new FaceDetector.Face[5];
                    final int count = faceDetector.findFaces(resource, faces);
                    Log.e("bindData", "count: " +  count);
                }
            });
        }
    }

    private SpannableStringBuilder getStyle() {
        final SpannableStringBuilder style = new SpannableStringBuilder(getString(R.string.moduleverify_string_security_verify));
        style.append("\n").append(getString(R.string.moduleverify_string_security_prove)).append(mName).append(getString(R.string.moduleverify_string_security_exam));
        final int nameLength = mName.length();
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.modulebase_color_btn_yellow_normal)), 12, 12 + nameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new AbsoluteSizeSpan(18, true), 12, 12 + nameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
    }

    private void getExtraData() {
        final Intent intent = getIntent();
        this.mPreviewPath = intent.getStringExtra(VerifyConsts.EXTRA_PREVIEW_PATH);
        this.mName = intent.getStringExtra(VerifyConsts.EXTRA_PREVIEW_NAME);
    }

    private void onRestoreState() {
        finish();
    }

    private void showSystemSetting() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.moduleverify_string_security_dialog_tips));
        builder.setMessage(getString(R.string.moduleverify_string_security_dialog_system));
        builder.setPositiveButton(getString(R.string.moduleverify_string_security_dialog_confirm), null);
        builder.create().show();
    }

    private void showRequestPermissionRationale() {
        final Window window = getWindow();
        Snackbar.make(window.getDecorView(), getString(R.string.moduleverify_string_security_snackbar_tips), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.moduleverify_string_security_dialog_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(FacePreviewActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                }).show();
    }

    private void beforeStartVerify() {
        requesCameraPermissions(new PermissionCallback() {
            @Override
            public void onGranted() {
                startVerify();
            }
        });
    }

    public interface PermissionCallback {

        void onGranted();
    }

    private void requesCameraPermissions(PermissionCallback callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.onGranted();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            callback.onGranted();
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }
}
