package com.longrise.android.face.verify.assist;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by godliness on 2020-07-07.
 *
 * @author godliness
 */
public final class VerifyHelper {

    public interface RequestCode {
        int VERIFY = 1033;
        int UPLOAD_PHOTO = 1034;
    }

    public interface Extra {
        String PHOTO_URL = "extra_photo_url";
        String PHOTO_NAME = "extra_photo_name";
    }

    public static boolean getVerifyResult(int resultCode) {
        return resultCode == Activity.RESULT_OK;
    }

    @Nullable
    public static String getUploadPhotoResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            return data.getStringExtra(Extra.PHOTO_URL);
        }
        return null;
    }
}
