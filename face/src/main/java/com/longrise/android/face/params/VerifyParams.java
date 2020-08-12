package com.longrise.android.face.params;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by godliness on 2020-08-09.
 *
 * @author godliness
 */
public final class VerifyParams implements Parcelable {

    public static final String EXTRA_CARD_PARAMS = "extra_verify_params";

    private String mName;
    private String mPreviewUrl;

    public void name(String name) {
        this.mName = name;
    }

    public void previewUrl(String url) {
        this.mPreviewUrl = url;
    }

    public String name() {
        return mName;
    }

    public String previewUrl() {
        return mPreviewUrl;
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
