package com.longrise.android.face.verify.utils;

import android.graphics.Bitmap;
import android.media.FaceDetector;

/**
 * Created by godliness on 2020-08-06.
 *
 * @author godliness
 */
public final class FaceUtil {

    public static boolean foundFaces(Bitmap src) {
        return foundFaces(src, 1);
    }

    public static boolean foundFaces(Bitmap src, int maxFaces) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final FaceDetector detector = new FaceDetector(width, height, maxFaces);
        return detector.findFaces(src, new FaceDetector.Face[maxFaces]) > 0;
    }
}
