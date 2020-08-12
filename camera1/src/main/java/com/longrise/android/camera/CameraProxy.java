package com.longrise.android.camera;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.support.annotation.Nullable;
import android.view.Surface;

import java.io.IOException;
import java.util.List;

/**
 * Created by godliness on 2020-06-26.
 *
 * @author godliness
 */
@SuppressWarnings("unused")
final class CameraProxy {

    private static final String TAG = "CameraProxy";

    static Camera createCamera(int cameraId) throws RuntimeException {
        return Camera.open(cameraId);
    }

    static Camera.Size calcOptimaSize(List<Camera.Size> sizes, int expectWidth, int expectHeight) {
        if (expectWidth < expectHeight) {
            final int oldWidth = expectWidth;
            expectWidth = expectHeight;
            expectHeight = oldWidth;
        }
        int diffs = Integer.MAX_VALUE;
        Camera.Size bestSize = null;
        for (Camera.Size size : sizes) {
            final int diffWidth = Math.abs(size.width - expectWidth);
            final int diffHeight = Math.abs(size.height - expectHeight);
            final int newDiff = diffWidth + diffHeight;
            if (newDiff == 0) {
                bestSize = size;
                break;
            } else if (diffs > newDiff) {
                diffs = newDiff;
                bestSize = size;
            }
        }
        return bestSize;
    }

    static int getSupportPreviewFormat(Camera.Parameters parameters) {
        final List<Integer> previewFormats = parameters.getSupportedPreviewFormats();
        int nv21 = ImageFormat.UNKNOWN;
        int yv12 = ImageFormat.UNKNOWN;
        for (Integer format : previewFormats) {
            if (format == ImageFormat.NV21) {
                nv21 = ImageFormat.NV21;
            } else if (format == ImageFormat.YV12) {
                yv12 = ImageFormat.YV12;
            }
        }
        return nv21 != ImageFormat.UNKNOWN ? nv21 : yv12;
    }

    static String getSupportFocusMode(Camera.Parameters parameters, String expectMode) {
        final List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.contains(expectMode)) {
            return expectMode;
        }
        if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            return Camera.Parameters.FOCUS_MODE_AUTO;
        }
        return supportedFocusModes.get(0);
    }

    static boolean isSupportFocusMode(Camera.Parameters parameters, String expectMode) {
        final List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        return supportedFocusModes.contains(expectMode);
    }

    @Nullable
    static int[] getSupportedPreviewFpsRange(Camera.Parameters parameters, int min, int max) {
        if (min <= 0 || max <= 0) {
            return null;
        }
        return calcPreviewFpsRange(parameters, min, max);
    }

    static int[] calcPreviewFpsRange(Camera.Parameters parameters, int min, int max) {
        final List<int[]> fpsRanges = parameters.getSupportedPreviewFpsRange();
        int diffs = Integer.MAX_VALUE;
        int[] bestFpsRange = null;
        for (int[] fpsRange : fpsRanges) {
            final int diffMin = Math.abs(fpsRange[0] - min);
            final int diffMax = Math.abs(fpsRange[1] - max);
            final int newDiff = diffMin + diffMax;
            if (newDiff == 0) {
                bestFpsRange = fpsRange;
                break;
            } else if (diffs > newDiff) {
                diffs = newDiff;
                bestFpsRange = fpsRange;
            }
        }
        return bestFpsRange;
    }

    static int getDisplayOrientation(Activity host, int cameraId) {
        if (host == null) {
            return 0;
        }
        final Camera.CameraInfo info = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(cameraId, info);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return calcDisplayOrientation(info, host
                .getWindowManager()
                .getDefaultDisplay()
                .getRotation());
    }

    static int calcDisplayOrientation(Camera.CameraInfo info, int displayRotation) {
        int degrees = 0;
        switch (displayRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    static void releaseCamera(Camera camera) {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
        }
    }

    static int getBitmapDegreeFromFile(String path) {
        int degree = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exifInterface != null) {
            final int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        }
        return degree;
    }

    static boolean checkCameraService(Context context) {
        final DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return !dpm.getCameraDisabled(null);
    }
}
