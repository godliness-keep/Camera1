package com.longrise.android.face.verify.verify;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by godliness on 2020-08-06.
 *
 * @author godliness
 */
public final class PhotoParams implements Parcelable {

    public static final String EXTRA_CARD_PARAMS = "extra_card_params";

    /**
     * 证件照地址，注意只要非 null 即视为有证件照
     */
    public String mCardUrl;
    /**
     * 提示信息
     */
    public String mTips;
    /**
     * 是否存在证件照
     */
    boolean mHasCard;

    public PhotoParams() {

    }

    protected PhotoParams(Parcel in) {
        mCardUrl = in.readString();
        mTips = in.readString();
        mHasCard = !TextUtils.isEmpty(mCardUrl);
    }

    public static final Creator<PhotoParams> CREATOR = new Creator<PhotoParams>() {
        @Override
        public PhotoParams createFromParcel(Parcel in) {
            return new PhotoParams(in);
        }

        @Override
        public PhotoParams[] newArray(int size) {
            return new PhotoParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCardUrl);
        dest.writeString(mTips);
        dest.writeByte((byte) (TextUtils.isEmpty(mCardUrl) ? 0 : 1));
    }
}
