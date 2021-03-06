package com.longrise.android.face.verify;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.longrise.android.face.verify.assist.TimerAssist;


/**
 * Created by godliness on 2020-07-07.
 *
 * @author godliness
 */
public final class FaceVerifyProxy implements Handler.Callback {

    private static final String TAG = "FaceVerifyProxy";
    private static final int MSG_QUERY_MATCH_STATE = 0;

    private final int MAX_QUERY_COUNT = 2;
    private int mRetryCount;

    private FaceUploadCallback mUploadCallback;
    private FaceMatchCallback mMatchCallback;
    private FaceProxyListener mProxyListener;

    private final Handler mHandler;
    private final int[] mDelayRetrys;
    private boolean mDestroy;

    FaceVerifyProxy(FaceProxyListener proxyListener) {
        this(proxyListener, new int[]{200, 800, 1600});
    }

    FaceVerifyProxy(FaceProxyListener proxyListener, int[] delays) {
        this.mProxyListener = proxyListener;
        this.mHandler = new Handler(this);
        this.mDelayRetrys = delays;
    }

    void uploadFaceToService(String base64) {
        FaceMatchRegistry.getRegistry().uploadFacePhoto(base64, getUploadCallback());
    }

    void queryMatchResult(String id) {
        printLog(id);
        queryMatchResult(id, mDelayRetrys[0]);
    }

    void destroy() {
        mDestroy = true;
        mHandler.removeCallbacksAndMessages(null);
        mProxyListener = null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mDestroy) {
            return true;
        }
        if (msg.what == MSG_QUERY_MATCH_STATE) {
            final String id = (String) msg.obj;
            FaceMatchRegistry.getRegistry().queryFaceMatchResult(id, getMatchCallback());
        }
        return true;
    }

    private void queryMatchResult(String id, int delayTime) {
        final Message queryMsg = mHandler.obtainMessage(MSG_QUERY_MATCH_STATE);
        queryMsg.obj = id;
        mHandler.sendMessageDelayed(queryMsg, delayTime);
    }

    private FaceUploadCallback getUploadCallback() {
        if (mUploadCallback == null) {
            mUploadCallback = new FaceUploadCallback() {
                @Override
                public void uploadFaceSuccess(String id) {
                    if (!mDestroy) {
                        queryMatchResult(id, mDelayRetrys[0]);
                    }
                    // 创建匹配查询倒计时器
                    TimerAssist.createPlayerTimer(id);

                    printLog("uploadFaceSuccess");
                }

                @Override
                public void uploadFaceFailed(String msg) {
                    if (mDestroy) {
                        return;
                    }
                    if (mProxyListener != null) {
                        mProxyListener.verifyFailed(msg);
                    }

                    printLog("uploadFaceFailed");
                }
            };
        }
        return mUploadCallback;
    }

    private FaceMatchCallback getMatchCallback() {
        if (mMatchCallback == null) {
            mMatchCallback = new FaceMatchCallback() {
                @Override
                public void faceMatchSuccess(String... msg) {
                    if (mDestroy) {
                        TimerAssist.stagingMatchResult(true, msg);
                    } else {
                        if (mProxyListener != null) {
                            mProxyListener.verifySuccess(msg);
                        }
                        TimerAssist.stopPlayerTimer();
                    }
                    // 查询次数清零
                    mRetryCount = 0;

                    printLog("faceMatchSuccess: " + mRetryCount);
                }

                @Override
                public void faceMatchFailed(String msg) {
                    if (mDestroy) {
                        TimerAssist.stagingMatchResult(false, msg);
                    } else {
                        if (mProxyListener != null) {
                            mProxyListener.verifyFailed(msg);
                        }
                        TimerAssist.stopPlayerTimer();
                    }
                    // 查询次数清零
                    mRetryCount = 0;

                    printLog("faceMatchFailed: " + mRetryCount);
                }

                @Override
                public void retryGetMatchState(String id, String msg) {
                    if (mDestroy) {
                        return;
                    }
                    if (mRetryCount < MAX_QUERY_COUNT) {
                        mRetryCount++;
                        queryMatchResult(id, mDelayRetrys[mRetryCount]);
                    } else {
                        if (mProxyListener != null) {
                            mProxyListener.verifyTimeout();
                        }
                        mRetryCount = 0;
                    }

                    printLog("retryGetMatchState: " + mRetryCount);
                }
            };
        }
        return mMatchCallback;
    }

    public interface FaceProxyListener {

        /**
         * 识别超时
         */
        void verifyTimeout();

        /**
         * 识别失败，可能是网络、也可能服务
         */
        void verifyFailed(String msg);

        /**
         * 识别成功
         */
        void verifySuccess(String... msg);
    }

    public interface FaceUploadCallback {

        /**
         * 通知上传成功
         */
        void uploadFaceSuccess(String id);

        /**
         * 上传失败
         */
        void uploadFaceFailed(String msg);
    }

    public interface FaceMatchCallback {

        /**
         * 匹配成功
         */
        void faceMatchSuccess(String... msg);

        /**
         * 匹配失败
         */
        void faceMatchFailed(String msg);

        /**
         * 重新尝试
         */
        void retryGetMatchState(String id, String msg);
    }

    private void printLog(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
