package com.longrise.android.face.verify;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by godliness on 2020-07-07.
 *
 * @author godliness
 * 通过注册回调添加请求服务
 * todo 与Activity 的交互没有太好的方式，这种方式仍然存在 Activity 回收的问题
 * todo 通过暴力的直接finish 解决回收问题
 */
public final class FaceMatchRegistry {

    private static volatile FaceMatchRegistry sRegistry;

    private FaceUploadListener mUpladListener;
    private FaceMatchListener mMatchListener;

    public interface FaceUploadListener {

        /**
         * 完善照片上传
         */
        void onUploadFacePhoto(String base64, @NonNull FaceVerifyProxy.FaceUploadCallback callback);
    }

    public interface FaceMatchListener {

        /**
         * 完善匹配结果查询
         */
        void onMatchResult(final String id, @NonNull FaceVerifyProxy.FaceMatchCallback callback);
    }

    public static FaceMatchRegistry getRegistry() {
        if (sRegistry == null) {
            synchronized (FaceMatchRegistry.class) {
                if (sRegistry == null) {
                    sRegistry = new FaceMatchRegistry();
                }
            }
        }
        return sRegistry;
    }

    public static void unRegistry() {
        sRegistry = null;
    }

    /**
     * 注册上传通知
     */
    public void registerUploadListener(FaceUploadListener uploadListener) {
        this.mUpladListener = uploadListener;
    }

    /**
     * 注册查询结果通知
     */
    public void registerMatchListener(FaceMatchListener matchListener) {
        this.mMatchListener = matchListener;
    }

    void queryFaceMatchResult(final String id, @NonNull FaceVerifyProxy.FaceMatchCallback callback) {
        if (mMatchListener != null) {
            mMatchListener.onMatchResult(id, callback);
        }
    }

    void uploadFacePhoto(String base64, @NonNull FaceVerifyProxy.FaceUploadCallback callback) {
        if (mUpladListener != null) {
            mUpladListener.onUploadFacePhoto(base64, callback);
        }
    }
}
