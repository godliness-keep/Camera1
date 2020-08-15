package com.longrise.android.camera1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

//        final CameraPreview preview = findViewById(R.id.camera_preview);
//        final CameraConfig config = preview.openPreview();
//        config.params(mParamsCallback);
//        config.takePicture(null, null, mJpegCallback);
//        config.previewStatusListener(mPreviewStatusListener);
//        config.faceDetectionListener(mFaceDetectionListener);
//
//
//        findViewById(R.id.support_face_detection).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "supportFaceDetection: " + preview.isSupportFaceDetection());
//            }
//        });
//
//        findViewById(R.id.tv_take_picture).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                preview.takePicture();
//                preview.restartPreview();
//            }
//        });
    }
//
//    private final ParamsCallback mParamsCallback = new ParamsCallback() {
//        @Override
//        public CameraParams params() {
//            final CameraParams params = new CameraParams();
//            params.cameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
//            return params;
//        }
//    };
//
//    private final Camera.FaceDetectionListener mFaceDetectionListener = new Camera.FaceDetectionListener() {
//        @Override
//        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
//            Log.e(TAG, "faces length: " + faces.length);
//        }
//    };
//
//    private final PreviewStatusListener mPreviewStatusListener = new PreviewStatusListener() {
//        @Override
//        public void onPreviewStatus(int status, String extra) {
//            Log.e(TAG, "status: " + status + " extra: " + extra);
//        }
//
//        @Override
//        public void onCameraOpened(Camera.Parameters basic) {
//
//        }
//    };
//
//    private final JpegCallback mJpegCallback = new JpegCallback() {
//        @Override
//        public void onJpegTaken(byte[] data, Camera camera) {
//            final Bitmap pic = BitmapFactory.decodeByteArray(data, 0, data.length);
//            Log.e(TAG, "width: " + pic.getWidth() + " height: " + pic.getHeight());
//        }
//    };

}
