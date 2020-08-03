package org.dp.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.MatOfRect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by godliness on 2020-08-03.
 *
 * @author godliness
 * https://github.com/ShiqiYu/libfacedetection
 * https://github.com/onlyloveyd/Android-FaceDetection
 * 人脸检测
 */
public final class FaceDetect {

    static {
        System.loadLibrary("facedetection");
    }

    public static void init() {
        // do nothing
    }

    public Face[] findFaces(Bitmap src) {
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
