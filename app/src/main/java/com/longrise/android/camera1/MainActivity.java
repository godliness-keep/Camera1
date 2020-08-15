package com.longrise.android.camera1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.longrise.android.face.verify.FaceMatchRegistry;
import com.longrise.android.face.verify.FacePhotoActivity;
import com.longrise.android.face.verify.FacePreviewActivity;
import com.longrise.android.face.verify.FaceVerifyActivity;
import com.longrise.android.face.verify.FaceVerifyProxy;
import com.longrise.android.face.verify.verify.PhotoParams;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String PREVIEW_PATH = "https://wuhan.yxybb.com/BBV6/LEAP/Download/headimage/2019/10/28/92039a946f094544abd5eec115c2e678.jpg";

    public boolean isDateTimeAuto() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return true;
        }
        try {
            return Settings.Global.getInt(getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.face_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceVerifyActivity.openFaceVerify(MainActivity.this);
                registerListener();
            }
        });

        findViewById(R.id.preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 需要先开启预览效果
                FacePreviewActivity.openFacePreview(MainActivity.this, PREVIEW_PATH, "godliness");
                registerListener();
            }
        });

        findViewById(R.id.monitor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, MonitoringActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, FacePhotoActivity.class);
                final PhotoParams params = new PhotoParams();
                params.mTips = "温馨提示：\n①上传的照片用于您本人报名在线考试时的电子证件照；\n② 照片上传后如有问题，请联系客服400-681-8148。";
                intent.putExtra(PhotoParams.EXTRA_CARD_PARAMS, params);
                startActivity(intent);
            }
        });
    }

    private FaceMatchRegistry.FaceUploadListener mUploadListener;
    private FaceMatchRegistry.FaceMatchListener mMatchListener;

    private FaceMatchRegistry.FaceUploadListener getUploadListener() {
        if (mUploadListener == null) {
            mUploadListener = new FaceMatchRegistry.FaceUploadListener() {
                @Override
                public void onUploadFacePhoto(String base64, @NonNull FaceVerifyProxy.FaceUploadCallback callback) {

                }
            };
        }
        return mUploadListener;
    }

    private FaceMatchRegistry.FaceMatchListener getMatchListener() {
        if (mMatchListener == null) {
            mMatchListener = new FaceMatchRegistry.FaceMatchListener() {
                @Override
                public void onMatchResult(String id, @NonNull FaceVerifyProxy.FaceMatchCallback callback) {

                }
            };
        }
        return mMatchListener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
