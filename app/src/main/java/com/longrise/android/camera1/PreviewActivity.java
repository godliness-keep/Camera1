package com.longrise.android.camera1;

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
import com.longrise.android.camera.preview.CameraConfig;
import com.longrise.android.camera.preview.CameraPreview;
import com.longrise.android.camera.preview.ParamsCallback;
import com.longrise.android.camera.preview.PreviewStatusListener;

/**
 * Created by godliness on 2020-07-06.
 *
 * @author godliness
 */
public final class PreviewActivity extends AppCompatActivity {

    private CameraPreview mPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(R.layout.activity_preview);

        mPreview = findViewById(R.id.preview);
        // 开启预览，并配置所需
        CameraConfig config = mPreview.openPreview();
        config.params(mParamsCallback);
        config.previewStatusListener(mStatusListener);
        config.takePicture(null, null, mJpegCallback);
    }

    private void takePicture() {
        // 拍照
        if (mPreview != null) {
            mPreview.takePicture();
        }

        // 重新预览
        if (mPreview != null) {
            mPreview.restartPreview();
        }
    }

    private final ParamsCallback mParamsCallback = new ParamsCallback() {
        @Override
        public CameraParams params() {
            return null;
        }
    };

    private final PreviewStatusListener mStatusListener = new PreviewStatusListener() {
        @Override
        public void onPreviewStatus(int status, String extra) {

        }

        @Override
        public void onCameraOpened(Camera.Parameters basic) {

        }
    };

    private final Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

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
