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
     * 打开的相机ID
     */
    public int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    /**
     * 图片宽度
     */
    public int mPictureWidth = 640;

    /**
     * 图片高度
     */
    public int mPictureHeight = 480;

    /**
     * 图片的质量
     */
    public int mImageQuality = 100;

    public CameraParams() {

    }

    protected CameraParams(Parcel in) {
        mCameraId = in.readInt();
        mPictureWidth = in.readInt();
        mPictureHeight = in.readInt();
        mImageQuality = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCameraId);
        dest.writeInt(mPictureWidth);
        dest.writeInt(mPictureHeight);
        dest.writeInt(mImageQuality);
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
