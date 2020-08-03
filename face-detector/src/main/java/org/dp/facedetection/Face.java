package org.dp.facedetection;

import org.opencv.core.Rect;

/**
 * Created by godliness on 2020-08-03.
 *
 * @author godliness
 * 识别出的人脸信息
 */
public final class Face {
    public Rect faceRect;
    public int faceConfidence;
    public int faceAngle;
}
