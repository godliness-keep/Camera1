package com.longrise.android.camera.preview;

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
public final class CameraProxy {

    @Nullable
    public static Camera createCamera(int cameraId) throws RuntimeException {
        return Camera.open(cameraId);
    }

    public static Camera.Size calcOptimaSize(List<Camera.Size> sizes, int expectWidth, int expectHeight) {
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

    public static int getSupportPreviewFormat(Camera.Parameters parameters) {
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
        return nv21 != -1 ? nv21 : yv12;
    }

    public static String getSupportFocusMode(Camera.Parameters parameters, String expectMode) {
        final List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.contains(expectMode)) {
            return expectMode;
        }
        if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            return Camera.Parameters.FOCUS_MODE_AUTO;
        }
        return supportedFocusModes.get(0);
    }

//    public static int[] getSupportedPreviewFpsRange(Camera.Parameters parameters, int min, int max) {
//        final List<int[]> fpsRanges = parameters.getSupportedPreviewFpsRange();
//        parameters.getSupportedPreviewFrameRates();
//
//    }

    public static int getSupportedPreviewFrameRates(Camera.Parameters parameters, int frameRate) {
        final List<Integer> rates = parameters.getSupportedPreviewFrameRates();
        if (rates.contains(frameRate)) {
            return frameRate;
        }
        int fitDiff = Integer.MAX_VALUE;
        int fitRate = 0;
        for (Integer rate : rates) {
            final int currentDiff = Math.abs(rate - frameRate);
            if (currentDiff < fitDiff) {
                fitDiff = currentDiff;
                fitRate = rate;
            }
        }
        return fitRate;
    }

    public static int getDisplayOrientation(Activity host,
                                            int cameraId) {
        if (host == null) {
            return 0;
        }
        final Camera.CameraInfo info = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(cameraId, info);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int rotation = host.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
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

    public static void releaseCamera(Camera camera) {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
        }
    }

    public static int getBitmapDegreeFromFile(String path) {
        int degree = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exifInterface != null) {
            // 获取图片的旋转信息
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

    public static boolean checkCameraService(Context context) {
        final DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return !dpm.getCameraDisabled(null);
    }


}
