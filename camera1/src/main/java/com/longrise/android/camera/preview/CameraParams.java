package com.longrise.android.camera.preview;

import android.hardware.Camera;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by godliness on 2020-07-04.
 *
 * @author godliness
 */
public final class CameraParams implements Parcelable {

    public static final String EXTRA_PREVIEW_PARAMS = "extra_preview_params";

    int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    int mPictureWidth = 640;
    int mPictureHeight = 480;
    int mImageQuality = 100;
    int mMinFps;
    int mMaxFps;

    /**
     * Camera.Parameters.FOCUS_MODE_AUTO
     */
    String mFocusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

    /**
     * 相机ID
     */
    public void cameraId(int cameraId) {
        this.mCameraId = cameraId;
    }

    /**
     * 拍照图片尺寸
     */
    public void pictureSize(int width, int height) {
        this.mPictureWidth = width;
        this.mPictureHeight = height;
    }

    /**
     * 拍照图片质量
     */
    public void imageQuality(int imageQuality) {
        this.mImageQuality = imageQuality;
    }

    /**
     * 预览帧率，建议最小值不低于24fps，即24000
     */
    public void fpsRange(int min, int max) {
        this.mMinFps = min;
        this.mMaxFps = max;
    }

    /**
     * 对焦模式
     */
    public void focusMode(String focusMode) {
        this.mFocusMode = focusMode;
    }

    public CameraParams() {

    }

    protected CameraParams(Parcel in) {
        mCameraId = in.readInt();
        mPictureWidth = in.readInt();
        mPictureHeight = in.readInt();
        mImageQuality = in.readInt();
        mMinFps = in.readInt();
        mMaxFps = in.readInt();
        mFocusMode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCameraId);
        dest.writeInt(mPictureWidth);
        dest.writeInt(mPictureHeight);
        dest.writeInt(mImageQuality);
        dest.writeInt(mMinFps);
        dest.writeInt(mMaxFps);
        dest.writeString(mFocusMode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CameraParams> CREATOR = new Creator<CameraParams>() {
        @Override
        public CameraParams createFromParcel(Parcel in) {
            return new CameraParams(in);
        }

        @Override
        public CameraParams[] newArray(int size) {
            return new CameraParams[size];
        }
    };

}
