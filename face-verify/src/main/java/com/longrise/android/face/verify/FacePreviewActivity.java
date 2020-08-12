package com.longrise.android.face.verify;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.longrise.android.face.PreviewFragment;
import com.longrise.android.face.PreviewProxy;
import com.longrise.android.face.verify.assist.VerifyHelper;
import com.longrise.android.face.verify.verify.PhotoParams;

/**
 * Created by godliness on 2020-07-07.
 *
 * @author godliness
 * 面部预览
 */
public final class FacePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CAMERA_PERMISSION = 101;

    private PreviewProxy mProxy;

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
        intent.putExtra(VerifyHelper.Extra.PHOTO_URL, previewPath);
        intent.putExtra(VerifyHelper.Extra.PHOTO_NAME, name);
        host.startActivityForResult(intent, VerifyHelper.RequestCode.VERIFY);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getExtraData();
        } else {
            onRestoreState();
        }
        setContentView(R.layout.moduleverify_activity_face_preivew);
        findViewById(R.id.iv_back).setOnClickListener(this);

        // 创建预览
        mProxy = new PreviewFragment.Builder(this)
                .facePreviewListener(mPreviewCallback)
                .name(mName)
                .previewUrl(mPreviewPath)
                .detectLevel(PreviewFragment.DetectLevel.ALL)
                .commitAndSaveState(savedInstanceState, R.id.preview_content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VerifyHelper.RequestCode.VERIFY:
                if (VerifyHelper.getVerifyResult(resultCode)) {
                    setSuccessToResult();
                }
                break;

            case VerifyHelper.RequestCode.UPLOAD_PHOTO:
                final String newPhoto = VerifyHelper.getUploadPhotoResult(resultCode, data);
                if (newPhoto != null) {
                    if (mProxy != null) {
                        mProxy.restartPreview(newPhoto);
                    }
                }
                break;

            default:
                break;
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
        if (v.getId() == R.id.iv_back) {
            finish();
        }
    }

    private final PreviewFragment.OnFacePreviewListener mPreviewCallback = new PreviewFragment.OnFacePreviewListener() {
        @Override
        public void onNotFoundFace() {
            toReUploadPhoto(getString(R.string.moduleverify_string_face_not_detected));
        }

        @Override
        public void onMoreThanSize(int srcWidth, int srcHeight) {
            toReUploadPhoto(getString(R.string.moduleverify_string_face_size));
        }

        @Override
        public void toVerify() {
            beforeStartVerify();
        }
    };

    private void toReUploadPhoto(String msg) {
        new AlertDialog.Builder(FacePreviewActivity.this)
                .setMessage(msg)
                .setPositiveButton(R.string.moduleverify_string_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final Intent intent = new Intent(FacePreviewActivity.this, FacePhotoActivity.class);
                        intent.putExtra(PhotoParams.EXTRA_CARD_PARAMS, new PhotoParams());
                        startActivityForResult(intent, VerifyHelper.RequestCode.UPLOAD_PHOTO);
                    }
                }).setNegativeButton(R.string.moduleverify_string_verify_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void setSuccessToResult() {
        final Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void startVerify() {
        FaceVerifyActivity.openFaceVerify(this);
    }

    private void getExtraData() {
        final Intent intent = getIntent();
        this.mPreviewPath = intent.getStringExtra(VerifyHelper.Extra.PHOTO_URL);
        this.mName = intent.getStringExtra(VerifyHelper.Extra.PHOTO_NAME);
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

    private interface PermissionCallback {

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
