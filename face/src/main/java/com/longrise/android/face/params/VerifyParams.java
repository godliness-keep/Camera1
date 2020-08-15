package com.longrise.android.face.params;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;

import com.longrise.android.face.assist.FaceHelper;

/**
 * Created by godliness on 2020-08-09.
 *
 * @author godliness
 */
public final class VerifyParams implements Parcelable {

    public static final String EXTRA_CARD_PARAMS = "extra_verify_params";

    private String mName;
    private String mPreviewUrl;
    private int mMaxWidth = FaceHelper.MAX_FACE_WIDTH;
    private int mMaxHeight = FaceHelper.MAX_FACE_HEIGHT;

    /**
     * 姓名
     */
    public void name(String name) {
        this.mName = name;
    }

    /**
     * 预览图片地址
     */
    public void previewUrl(String url) {
        this.mPreviewUrl = url;
    }

    /**
     * 检查图片尺寸是否超过该值，否则拦截{@link com.longrise.android.face.PreviewFragment}
     */
    public void photoSize(int maxWidth, int maxHeight) {
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
    }

    public String name() {
        return mName;
    }

    public String previewUrl() {
        return mPreviewUrl;
    }

    public int photoMaxWidth() {
        return mMaxWidth;
    }

    public int photoMaxHeight() {
        return mMaxHeight;
    }

    public VerifyParams() {

    }

    protected VerifyParams(Parcel in) {
        mName = in.readString();
        mPreviewUrl = in.readString();
    }

    public static final Creator<VerifyParams> CREATOR = new Creator<VerifyParams>() {
        @Override
        public VerifyParams createFromParcel(Parcel in) {
            return new VerifyParams(in);
        }

        @Override
        public VerifyParams[] newArray(int size) {
            return new VerifyParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPreviewUrl);
    }
}
