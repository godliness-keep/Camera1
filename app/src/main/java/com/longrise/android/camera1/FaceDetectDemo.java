package com.longrise.android.camera1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.dp.facedetection.Face;
import org.dp.facedetection.FaceDetect;

/**
 * Created by godliness on 2020-08-03.
 *
 * @author godliness
 * 人脸检测 Demo
 */
public final class FaceDetectDemo extends AppCompatActivity {

    private static final String TAG = "FaceDetectDemo";

    private ImageView mPreview;
    private TextView mNums;

    static {
        FaceDetect.init();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mPreview = findViewById(R.id.preview);
        mNums = findViewById(R.id.face_nums);

        findViewById(R.id.detector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceDetector();
            }
        });
    }

    private void faceDetector() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        final Bitmap newBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.have_nums_facus, options);
        mPreview.setImageBitmap(newBitmap);

        final long start = System.currentTimeMillis();
        final FaceDetect detect = new FaceDetect();
        final Face[] faces = detect.findFaces(newBitmap);
        if (faces != null) {
            mNums.setText("检测到人脸数量为：" + faces.length);
        }

        Log.e(TAG, "delay: " + (System.currentTimeMillis() - start));
    }
}
