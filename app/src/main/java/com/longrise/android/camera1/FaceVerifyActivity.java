package com.longrise.android.camera1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.longrise.android.camera.CameraParams;
import com.longrise.android.camera.FaceBuilder;
import com.longrise.android.camera.preview.ParamsCallback;
import com.longrise.android.camera.preview.PreviewProxy;
import com.longrise.android.camera.preview.PreviewStatusListener;
import com.longrise.android.camera.preview.Status;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by godliness on 2020-07-05.
 *
 * @author godliness
 */
public final class FaceVerifyActivity extends AppCompatActivity {

    private PreviewProxy mProxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();

        mProxy = new FaceBuilder(this)
                .params(mParamsCallback)
                .previewCallback(mStatusListener)
                .pictureCallback(null, null, mJpegCallback)
                .translucentStatus()
                .commitAndSaveState(savedInstanceState, Window.ID_ANDROID_CONTENT);

    }

    /**
     * 配置相机相关参数
     * 例如：拍照尺寸、相机ID、图片质量等
     */
    private final ParamsCallback mParamsCallback = new ParamsCallback() {
        @Override
        public CameraParams params() {
            final CameraParams params = new CameraParams();
            params.mPictureWidth = 640;
            params.mPictureHeight = 480;
            return params;
        }
    };

    /**
     * 拍照回调
     */
    private final Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            final File file = new File(getExternalFilesDir("jpeg"), "face.jpg");
            FileOutputStream fos;
            BufferedOutputStream bos = null;
            try {
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, 4096);
                bos.write(data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            setResult(file.getPath());
        }
    };

    /**
     * 恢复状态
     */
    private void resetStatus() {
        if (mProxy != null) {
            // 拍照结束后，通知重新预览
            mProxy.restartPreview();
            // 通知相关状态
            mProxy.notifyVerifyFailed("啊哦，识别失败了哟，请您重新尝试");
        }
    }

    private void setResult(String path) {
        final Intent intent = new Intent();
        intent.putExtra("path", path);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * 预览状态监听
     */
    private final PreviewStatusListener mStatusListener = new PreviewStatusListener() {
        @Override
        public void onPreviewStatus(int status, String extra) {
            switch (status) {
                case Status.CAMERA_OPEN_FAILED:
                    // 相机开启失败，属于不可逆错误，应当弹窗提示并退出
                    break;

                case Status.CAMERA_PREVIEW_FAILED:
                    // 相机预览失败，应该弹窗提示并退出
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onCameraOpened(Camera.Parameters basic) {
            // 相机已经打开，可以对相机进行进一步配置
        }
    };

    private void beforeSetContentView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}
