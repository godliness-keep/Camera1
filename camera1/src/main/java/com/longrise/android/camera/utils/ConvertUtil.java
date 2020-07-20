package com.longrise.android.camera.utils;

/**
 * Created by godliness on 2020-07-11.
 *
 * @author godliness
 */
public final class ConvertUtil {

    /**
     * 顺时针旋转90度
     * Y数据：yuv[i] = data[y * imageWidth + x]
     * UV数据：
     * 需求：后置镜头，数据需要顺时针旋转90度才显示正常。
     * 备注：这里的宽和高，都是原始影像中的宽和高。
     */
    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {

        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    /**
     * 顺时针旋转270度 + 水平镜像翻转
     * Y数据：
     * UV数据：
     * 需求：前置镜头，数据需要顺时针旋转270度，再水平镜像翻转才显示正常。
     * 备注：这里的宽和高，都是原始影像中的宽和高。
     */
    private byte[] rotateYUVDegree270AndMirror(byte[] data, int imageWidth, int imageHeight) {

        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate and mirror the Y luma
        int i = 0;
        int maxY = 0;
        for (int x = imageWidth - 1; x >= 0; x--) {
            maxY = imageWidth * (imageHeight - 1) + x * 2;
            for (int y = 0; y < imageHeight; y++) {
                yuv[i] = data[maxY - (y * imageWidth + x)];
                i++;
            }
        }
        // Rotate and mirror the U and V color components
        int uvSize = imageWidth * imageHeight;
        i = uvSize;
        int maxUV = 0;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            maxUV = imageWidth * (imageHeight / 2 - 1) + x * 2 + uvSize;
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[maxUV - 2 - (y * imageWidth + x - 1)];
                i++;
                yuv[i] = data[maxUV - (y * imageWidth + x)];
                i++;
            }
        }
        return yuv;
    }

    /**
     * 将NV21格式数据转换为NV12格式数据
     * NV12与NV21类似，U 和 V 交错排列,不同在于UV顺序。
     * NV12: YYYYYYYY UVUV =>YUV420SP
     * NV21: YYYYYYYY VUVU =>YUV420SP
     */
    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {

        if (nv21 == null || nv12 == null) return;
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }
}
