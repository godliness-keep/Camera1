package com.longrise.android.face.verify;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.longrise.android.camera.BackInterceptListener;
import com.longrise.android.camera.FaceBuilder;
import com.longrise.android.camera.PreviewProxy;
import com.longrise.android.camera.TakeInterceptListener;
import com.longrise.android.camera.preview.CameraParams;
import com.longrise.android.camera.preview.JpegCallback;
import com.longrise.android.camera.preview.ParamsCallback;
import com.longrise.android.camera.preview.PreviewStatusListener;
import com.longrise.android.camera.preview.Status;
import com.longrise.android.face.verify.assist.TimerAssist;
import com.longrise.android.face.verify.common.VerifyConsts;

/**
 * Created by godliness on 2020-07-05.
 *
 * @author godliness
 * 面部识别
 */
public final class FaceVerifyActivity extends AppCompatActivity {

    private PreviewProxy mProxy;
    private FaceVerifyProxy mVerifyProxy;
    private FaceVerifyProxy.FaceProxyListener mProxyListener;

    private Runnable mDelayResult;

    private int mFaceNum;
    private boolean mVerifyResult;

    /**
     * 开启面部识别
     */
    public static void openFaceVerify(Activity host) {
        final Intent intent = new Intent(host, FaceVerifyActivity.class);
        host.startActivityForResult(intent, VerifyConsts.REQUEST_VERIFY_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        if (savedInstanceState != null) {
            onRestoreState();
        }

        // 创建面部识别Fragment
        mProxy = new FaceBuilder(this)
                .params(mParamsCallback)
                .previewStatusCallback(mStatusListener)
                .pictureCallback(null, null, mJpegCallback)
                .takeInterceptListener(mInterceptListener)
                .backInterceptListener(mBackInterceptListener)
                .faceDetectionListener(mFaceDetectionListener)
                .translucentStatus()
                .commitAndSaveState(savedInstanceState, Window.ID_ANDROID_CONTENT);

        // 创建识别代理，监听上传服务与匹配过程
        createVerifyProxy();
    }

    @Override
    protected void onDestroy() {
        if (mVerifyProxy != null) {
            mVerifyProxy.destroy();
        }
        removeDelayResult();
        super.onDestroy();
    }

    private void createVerifyProxy() {
        mProxyListener = new FaceVerifyProxy.FaceProxyListener() {

            /**
             * 超过查询次数*/
            @Override
            public void verifyTimeout() {
                if (mProxy != null) {
                    mProxy.notifyVerifyFailed(getString(R.string.moduleverify_string_query_max_count));
                }
            }

            /**
             * 验证失败*/
            @Override
            public void verifyFailed(String msg) {
                if (mProxy != null) {
                    mProxy.notifyVerifyFailed(msg);
                }
            }

            /**
             * 验证成功*/
            @Override
            public void verifySuccess(String... msg) {
                mVerifyResult = true;
                if (msg == null || msg.length <= 0) {
                    finish();
                } else {
                    if (mProxy != null) {
                        mProxy.notifyVerifySuccess(msg);
                    }
                    delaySetSuccessToResult();
                }
            }
        };
        mVerifyProxy = new FaceVerifyProxy(mProxyListener);
    }

    /**
     * 配置相机相关参数
     * 例如：拍照尺寸、相机ID、图片质量等
     */
    private final ParamsCallback mParamsCallback = new ParamsCallback() {
        @Override
        public CameraParams params() {
            final CameraParams params = new CameraParams();
            params.cameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
            return params;
        }
    };

    /**
     * 拍照回调
     */
    private final JpegCallback mJpegCallback = new JpegCallback() {
        @Override
        public void onJpegTaken(byte[] data, Camera camera) {
            if (mProxy != null) {
                mProxy.restartPreview();
                mProxy.hideTakePicture();
            }

            if (mVerifyProxy != null) {
                final String faceBase64 = Base64.encodeToString(data, Base64.DEFAULT);
                mVerifyProxy.uploadFaceToService(faceBase64);
            }
        }
    };

    /**
     * 返回拦截
     */
    private final BackInterceptListener mBackInterceptListener = new BackInterceptListener() {

        @Override
        public boolean interceptClickBack() {
            //若需要拦截则执行动作并return true
            return false;
        }
    };

    /**
     * 业务拦截
     */
    private final TakeInterceptListener mInterceptListener = new TakeInterceptListener() {

        @Override
        public boolean interceptTakePicture() {
            if (interceptUploadPicture()) {
                // 拦截复用面部识别排队
                return true;
            }
            // 拦截非人脸拍照
            return interceptFaceDetection();
        }
    };

    /**
     * 人脸检测
     */
    private final Camera.FaceDetectionListener mFaceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            mFaceNum = faces != null ? faces.length : 0;
        }
    };

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

    /**
     * 复用面部识别排队 30s
     */
    private boolean interceptUploadPicture() {
        if (TimerAssist.isIntercept()) {
            final TimerAssist.FaceMatchResult matchResult = TimerAssist.hasMatchResult();
            if (matchResult != null) {
                recycleMatchResult(matchResult);
            } else {
                mProxy.hideTakePicture();
                mVerifyProxy.queryMatchResult(TimerAssist.getFaceMatchId());
            }
            return true;
        }
        return false;
    }

    /**
     * 复用匹配结果
     */
    private void recycleMatchResult(TimerAssist.FaceMatchResult matchResult) {
        if (matchResult.matchResult()) {
            mProxyListener.verifySuccess(matchResult.desc());
        } else {
            mProxyListener.verifyFailed(matchResult.desc());
        }
        matchResult.recycle();
    }

    /**
     * 拦截非人脸拍照
     */
    private boolean interceptFaceDetection() {
        if (mProxy.isSupportFaceDetection()) {
            if (mFaceNum >= 1) {
                return false;
            } else {
                mProxy.notifyVerifyFailed(getString(R.string.moduleverify_string_face_in_range));
                return true;
            }
        }
        return false;
    }

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

    @Override
    public void finish() {
        final Intent intent = new Intent();
        intent.putExtra(VerifyConsts.RESULT_VERIFY_STATUS, mVerifyResult);
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    private Runnable getDelayResult() {
        if (mDelayResult == null) {
            mDelayResult = new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            };
        }
        return mDelayResult;
    }

    private void delaySetSuccessToResult() {
        final View temp = removeDelayResult();
        temp.postDelayed(getDelayResult(), PreviewProxy.TIP_TIME_OUT);
    }

    private View removeDelayResult() {
        final View temp = findViewById(Window.ID_ANDROID_CONTENT);
        if (mDelayResult != null) {
            temp.removeCallbacks(mDelayResult);
        }
        return temp;
    }

    private void onRestoreState() {
        finish();
    }
}
