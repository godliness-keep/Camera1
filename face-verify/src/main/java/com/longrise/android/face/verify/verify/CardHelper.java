package com.longrise.android.face.verify.verify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.longrise.android.face.verify.FaceVerifyActivity;
import com.longrise.android.face.verify.common.VerifyConsts;

/**
 * Created by godliness on 2020-08-06.
 *
 * @author godliness
 */
public final class CardHelper {

    /**
     * 开启画廊
     */
    public static void startGallery(Activity host) {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        host.startActivityForResult(intent, VerifyConsts.RequestCode.GALLERY);
    }

    /**
     * 获取画廊返回结果
     */
    @Nullable
    public static Uri getGalleryOnResult(@Nullable Intent result) {
        return result != null ? result.getData() : null;
    }

    /**
     * 开启拍照
     */
    public static void startTakePicture(Activity host) {
        final Intent intent = new Intent();
        host.startActivityForResult(intent, VerifyConsts.RequestCode.TAKE_PICTURE);
    }

    /**
     * 获取拍照返回结果
     */
    public static Uri getTakePicture(@Nullable Intent result) {
        return result != null ? result.getData() : null;
    }

    public static void showSwitchDialog() {

    }

    /**
     * 开启人脸识别
     */
    public void openFaceVerify(Activity host) {
        FaceVerifyActivity.openFaceVerify(host);
    }

    /**
     * 获取人脸识别结果
     */
    public static boolean getVerifyOnResult(@Nullable Intent result) {
        return result != null && result.getBooleanExtra(VerifyConsts.RESULT_VERIFY_STATUS, false);
    }
}
