package com.longrise.android.face.verify.verify;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by godliness on 2020-08-06.
 *
 * @author godliness
 */
public final class CardParams implements Parcelable {

    public static final String EXTRA_CARD_PARAMS = "extra_card_params";

    /**
     * 证件照地址
     */
    public String mCardUrl;
    /**
     * 提示信息
     */
    public String mTips;

    /**
     * 是否有证件照
     */
    boolean mHasCard;

    public CardParams(){

    }

    protected CardParams(Parcel in) {
        mCardUrl = in.readString();
        mTips = in.readString();
        mHasCard = in.readByte() != 0;
    }

    public static final Creator<CardParams> CREATOR = new Creator<CardParams>() {
        @Override
        public CardParams createFromParcel(Parcel in) {
            return new CardParams(in);
        }

        @Override
        public CardParams[] newArray(int size) {
            return new CardParams[size];
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
