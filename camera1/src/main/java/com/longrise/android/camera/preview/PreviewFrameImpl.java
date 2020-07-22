package com.longrise.android.camera.preview;

import android.hardware.Camera;

/**
 * Created by godliness on 2020-07-14.
 *
 * @author godliness
 */
final class PreviewFrameImpl implements Camera.PreviewCallback {

    private final PreviewFrameCallback mFrameCallback;
    private final int mWidth;
    private final int mHeight;
    private final int mPreviewFormat;

    PreviewFrameImpl(int width, int height, int previewFormat, PreviewFrameCallback frameCallback) {
        this.mFrameCallback = frameCallback;
        this.mWidth = width;
        this.mHeight = height;
        this.mPreviewFormat = previewFormat;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mFrameCallback != null) {
            mFrameCallback.onPreviewFrame(data, mWidth, mHeight, mPreviewFormat);
        }
    }
}
