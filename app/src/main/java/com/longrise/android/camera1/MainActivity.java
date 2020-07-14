package com.longrise.android.camera1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.longrise.android.face.verify.FaceMatchRegistry;
import com.longrise.android.face.verify.FacePreviewActivity;
import com.longrise.android.face.verify.FaceVerifyActivity;
import com.longrise.android.face.verify.FaceVerifyProxy;
import com.longrise.android.face.verify.common.VerifyConsts;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.face_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果直接需要面部识别
                FaceVerifyActivity.openFaceVerify(MainActivity.this);
                registerListener();
            }
        });

        findViewById(R.id.preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 需要先开启预览效果
                FacePreviewActivity.openFacePreview(MainActivity.this, null, "godliness");
                registerListener();
            }
        });

        fastExtract();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VerifyConsts.REQUEST_VERIFY_CODE) {
            if (data != null && data.getBooleanExtra(VerifyConsts.RESULT_VERIFY_STATUS, false)) {
                // todo 此时表明人脸识别通过
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // todo 切记反注册
        FaceMatchRegistry.unRegistry();
    }

    private void registerListener() {
        FaceMatchRegistry.getRegistry().registerMatchListener(new FaceMatchRegistry.FaceMatchListener() {
            @Override
            public void onMatchResult(String id, final FaceVerifyProxy.FaceMatchCallback callback) {
                Log.e(TAG, "onMatchResult");

                // todo 在这里完成查询匹配结果
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callback.faceMatchSuccess("匹配成功，给个提示信息");
                    }
                }, 2000);
            }
        });

        FaceMatchRegistry.getRegistry().registerUploadListener(new FaceMatchRegistry.FaceUploadListener() {
            @Override
            public void onUploadFacePhoto(String base64, @NonNull FaceVerifyProxy.FaceUploadCallback callback) {
                Log.e(TAG, "onUploadFacePhoto");

                // todo 在这里完成图片上传工作
                callback.uploadFaceSuccess("12345");
            }
        });
    }

    public void fastExtract() {
        final int[] values = {9, 8, 4, 5, 6, 2, 121, 12, 24, 46, 26, 29};
        final int[] selected = new int[5];
        int size = values.length - 1;
        final Random random = new Random();
        for (int i = 0; i < 5; i++) {
            final int currentIndex = random.nextInt(size - i);
            final int currentValue = values[currentIndex];
            selected[i] = currentValue;

            // 将当前位置替换为后面位置
            values[currentIndex] = values[size - i];
        }
    }
}
