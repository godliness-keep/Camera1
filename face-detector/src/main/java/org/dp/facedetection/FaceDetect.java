package org.dp.facedetection;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.opencv.BuildConfig;
import org.opencv.android.Utils;
import org.opencv.core.MatOfRect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by godliness on 2020-08-03.
 *
 * @author godliness
 * https://github.com/ShiqiYu/libfacedetection
 * https://github.com/onlyloveyd/Android-FaceDetection
 * 人脸检测
 */
public final class FaceDetect implements Handler.Callback {

    private static volatile FaceDetect sDetect;

    private final byte MSG_DIE_SELF = -1;

    private final Handler mHandler;
    private final ExecutorService mExecutor;
    private final LinkedBlockingQueue<Runnable> mQueue;

    interface FaceDetectListener {

        void detected(Face[] faces, boolean hasFace);
    }

    static {
        System.loadLibrary("facedetection");
    }

    public static abstract class Finder implements FaceDetectListener {

        private final Bitmap mSrc;

        public Finder(Bitmap src) {
            this.mSrc = src;
        }

        Bitmap getSrc() {
            return mSrc;
        }
    }

    public static void init() {
        // do nothing
    }

    public static FaceDetect create() {
        return createIfDetect();
    }

    public void findFaces(Finder finder) {
        mHandler.removeMessages(MSG_DIE_SELF);
        mExecutor.submit(new Entry(finder, this));
    }

    @Override
    public boolean handleMessage(Message msg) {
        destroy();
        return true;
    }

    void notify(Runnable notify) {
        mHandler.post(notify);
        if (mQueue.size() <= 0) {
            startSelfDestruct();
        }
    }

    private void destroy() {
        mExecutor.shutdown();
        mHandler.removeCallbacksAndMessages(null);
        sDetect = null;
        if (BuildConfig.DEBUG) {
            Log.e("FaceDetect", "destroy");
        }
    }

    private void startSelfDestruct() {
        mHandler.sendEmptyMessageDelayed(MSG_DIE_SELF, 1000 * 30);
    }

    private static final class Entry implements Runnable {

        private final Finder mFinder;
        private final FaceDetect mDetect;

        Entry(Finder finder, FaceDetect detect) {
            this.mFinder = finder;
            this.mDetect = detect;
        }

        @Override
        public void run() {
            final Face[] faces = mDetect.findFaces(mFinder.getSrc());
            mDetect.notify(new Runnable() {
                @Override
                public void run() {
                    // In ui thread
                    mFinder.detected(faces, faces != null && faces.length > 0);
                }
            });
        }
    }

    private static FaceDetect createIfDetect() {
        if (sDetect == null) {
            synchronized (FaceDetect.class) {
                if (sDetect == null) {
                    sDetect = new FaceDetect();
                }
            }
        }
        return sDetect;
    }

    private FaceDetect() {
        this.mHandler = new Handler(Looper.getMainLooper(), this);
        this.mQueue = new LinkedBlockingQueue<>();
        this.mExecutor = new ThreadPoolExecutor(1, 3, 30, TimeUnit.SECONDS, mQueue, mFactory);
    }

    private final ThreadFactory mFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "face-detect");
        }
    };

    private Face[] findFaces(Bitmap src) {
        final MatOfRect matOfRect = new MatOfRect();
        Utils.bitmapToMat(src, matOfRect);
        try {
            return faceDetect(matOfRect.getNativeObjAddr());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private native Face[] faceDetect(long matAddr);
}
