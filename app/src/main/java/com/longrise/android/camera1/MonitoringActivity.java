package com.longrise.android.camera1;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.longrise.android.camera.preview.CameraConfig;
import com.longrise.android.camera.preview.CameraParams;
import com.longrise.android.camera.preview.CameraPreview;
import com.longrise.android.camera.preview.ParamsCallback;
import com.longrise.android.camera.preview.PreviewFrameCallback;

/**
 * Created by godliness on 2020-07-14.
 *
 * @author godliness
 * 监控demo
 */
public final class MonitoringActivity extends AppCompatActivity {

    private static final String TAG = "MonitoringActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        final CameraPreview preview = findViewById(R.id.camera_preview);
        final CameraConfig config = preview.openPreview();
        config.params(mParamsCallback);
        config.previewCallback(mFrameCallback);
    }

    private final PreviewFrameCallback mFrameCallback = new PreviewFrameCallback() {
        @Override
        public void onPreviewFrame(byte[] data, int width, int height) {
            Log.e(TAG, "data: " + data.length + " width: " + width + " height: " + height);
        }
    };

    private final ParamsCallback mParamsCallback = new ParamsCallback() {
        @Override
        public CameraParams params() {
            final CameraParams params = new CameraParams();
            params.cameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
            return params;
        }
    };
}
