package com.longrise.android.camera.preview;

import android.hardware.Camera;

/**
 * Created by godliness on 2020-07-14.
 *
 * @author godliness
 */
public interface PreviewFrameCallback {

    /**
     * Called as preview frames are displayed.  This callback is invoked
     * on the event thread was called from.
     *
     * <p>If using the {@link android.graphics.ImageFormat#YV12} format,
     * refer to the equations in {@link Camera.Parameters#setPreviewFormat}
     * for the arrangement of the pixel data in the preview callback
     * buffers.
     *
     * @param data        the contents of the preview frame in the format defined
     *                    by {@link android.graphics.ImageFormat}, which can be queried
     *                    with {@link android.hardware.Camera.Parameters#getPreviewFormat()}.
     *                    If {@link android.hardware.Camera.Parameters#setPreviewFormat(int)}
     *                    is never called, the default will be the YCbCr_420_SP
     *                    (NV21) format.
     * @param width       the preview width.
     * @param height      thie preview height.
     * @param frameFormat this preview format {@link android.graphics.ImageFormat}
     */
    void onPreviewFrame(byte[] data, int width, int height, int frameFormat);
}
