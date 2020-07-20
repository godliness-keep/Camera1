package com.longrise.android.camera.preview;

import android.hardware.Camera;

/**
 * Created by godliness on 2020-07-14.
 *
 * @author godliness
 */
final class PreviewFrame implements Camera.PreviewCallback {

    private final PreviewFrameCallback mFrameCallback;
    private final int mWidth;
    private final int mHeight;

    PreviewFrame(int width, int height, PreviewFrameCallback frameCallback) {
        this.mFrameCallback = frameCallback;
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mFrameCallback != null) {
            mFrameCallback.onPreviewFrame(data, mWidth, mHeight);
        }
    }
}
