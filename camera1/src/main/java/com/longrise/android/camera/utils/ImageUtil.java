package com.longrise.android.camera.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;

/**
 * Created by godliness on 2020-06-27.
 *
 * @author godliness
 */
public final class ImageUtil {

    public static Bitmap getPortraitPicture(int cameraId, byte[] data) {
        final Bitmap origin = BitmapFactory.decodeByteArray(data, 0, data.length);
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(cameraId, cameraInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return origin;
        }
        return rotaingBitmapToPortrait(cameraId, cameraInfo.orientation, origin);
    }

    public static Bitmap getPortraitPicture(int cameraId, Bitmap origin) {
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(cameraId, cameraInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return origin;
        }
        return rotaingBitmapToPortrait(cameraId, cameraInfo.orientation, origin);
    }

    private static Bitmap rotaingBitmapToPortrait(int cameraId, int angle, Bitmap bitmap) {
        final Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            matrix.postScale(-1, 1);
        }
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
