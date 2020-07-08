package com.longrise.android.camera.preview;

import android.hardware.Camera;

/**
 * Created by godliness on 2020-07-07.
 *
 * @author godliness
 */
public interface JpegCallback {

    /**
     * Called when image data is available after a picture is taken.
     * The format of the data depends on the context of the callback
     * and {@link Camera.Parameters} settings.
     *
     * @param data   a byte array of the picture data
     * @param camera the Camera service object
     */
    void onJpegTaken(byte[] data, Camera camera);
}
