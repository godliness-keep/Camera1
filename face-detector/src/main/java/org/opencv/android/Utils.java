package org.opencv.android;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

public class Utils {

    /**
     * Converts Android Bitmap to OpenCV Mat.
     * <p>
     * This function converts an Android Bitmap image to the OpenCV Mat.
     * <br>'ARGB_8888' and 'RGB_565' input Bitmap formats are supported.
     * <br>The output Mat is always created of the same size as the input Bitmap and of the 'CV_8UC4' type,
     * it keeps the image in RGBA format.
     * <br>This function throws an exception if the conversion fails.
     * @param bmp is a valid input Bitmap object of the type 'ARGB_8888' or 'RGB_565'.
     * @param mat is a valid output Mat object, it will be reallocated if needed, so it may be empty.
     * @param unPremultiplyAlpha is a flag, that determines, whether the bitmap needs to be converted from alpha premultiplied format (like Android keeps 'ARGB_8888' ones) to regular one; this flag is ignored for 'RGB_565' bitmaps.
     */
    public static void bitmapToMat(Bitmap bmp, Mat mat, boolean unPremultiplyAlpha) {
        if (bmp == null)
            throw new IllegalArgumentException("bmp == null");
        if (mat == null)
            throw new IllegalArgumentException("mat == null");
        nBitmapToMat2(bmp, mat.nativeObj, unPremultiplyAlpha);
    }

    /**
     * Short form of the bitmapToMat(bmp, mat, unPremultiplyAlpha=false).
     * @param bmp is a valid input Bitmap object of the type 'ARGB_8888' or 'RGB_565'.
     * @param mat is a valid output Mat object, it will be reallocated if needed, so Mat may be empty.
     */
    public static void bitmapToMat(Bitmap bmp, Mat mat) {
        bitmapToMat(bmp, mat, false);
    }

    private static native void nBitmapToMat2(Bitmap b, long m_addr, boolean unPremultiplyAlpha);

    private static native void nMatToBitmap2(long m_addr, Bitmap b, boolean premultiplyAlpha);
}
