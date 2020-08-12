package com.longrise.android.face.params;

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

    private String mPhotoUrl;
    private String mTips;

    /**
     * 证件照地址，注意只要非 null 即视为有证件照
     */
    public void photoUrl(String url){
        this.mPhotoUrl = url;
    }

    /**
     * 提示信息
     */
    public void tips(String tips){
        this.mTips = tips;
    }

    public String photoUrl(){
        return mPhotoUrl;
    }

    public String tips(){
        return mTips;
    }

    public boolean hasCard(){
        return !TextUtils.isEmpty(mPhotoUrl);
    }

    public PhotoParams() {

    }

    protected PhotoParams(Parcel in) {
        mPhotoUrl = in.readString();
        mTips = in.readString();
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
        dest.writeString(mPhotoUrl);
        dest.writeString(mTips);
    }
}
