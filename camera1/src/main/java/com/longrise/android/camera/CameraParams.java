package com.longrise.android.camera;

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

    /**
     * 相机ID
     */
    public int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    /**
     * 拍照图片宽度
     */
    public int mPictureWidth = 640;
    /**
     * 拍照图片高度
     */
    public int mPictureHeight = 480;
    /**
     * 拍照图片质量
     */
    public int mImageQuality = 100;
    /**
     * 最小预览帧率
     */
    public int mMinFps;
    /**
     * 最大预览帧率
     */
    public int mMaxFps;
    /**
     * 对焦模式
     */
    public String mFocusMode = Camera.Parameters.FOCUS_MODE_AUTO;

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
