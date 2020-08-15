package com.longrise.android.face.assist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;

import com.longrise.android.face.R;
import com.longrise.android.face.common.FaceConsts;
import com.longrise.android.face.utils.FileUtil;
import com.longrise.android.image.crop.Crop;

import java.io.File;

/**
 * Created by godliness on 2020-08-06.
 *
 * @author godliness
 */
@SuppressWarnings("WeakerAccess")
public final class FaceHelper {

    /**
     * 2英寸：413 * 626
     * 基于宽度 413 计算：413 / 3
     * 基于高度 626 计算：626 / 4
     * 基于宽度的匹配：411：548
     * 基于高度的匹配：468：624
     */
    public static final int ASPECT_X = 411;
    public static final int ASPECT_Y = 548;

    public static final int MAX_FACE_WIDTH = ASPECT_X * 2;
    public static final int MAX_FACE_HEIGHT = ASPECT_Y * 2;

    /**
     * 开启画廊
     */
    public static void startGallery(@NonNull Fragment host) {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        host.startActivityForResult(intent, FaceConsts.RequestCode.GALLERY);
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
    public static void startCamera(@NonNull Fragment host) {
        final Context cxt = host.getContext();
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final File faceFile = new File(FileUtil.getFaceSaveFile(cxt), "camera.jpg");
        if (faceFile.exists()) {
            faceFile.delete();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(cxt, cxt.getPackageName() + ".fileprovider", faceFile);
        } else {
            uri = Uri.fromFile(faceFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        host.startActivityForResult(intent, FaceConsts.RequestCode.CAMERA);
    }

    /**
     * 获取拍照返回结果
     */
    @Nullable
    public static Uri getCameraOnResult(Activity host, int resultCode) {
        if (host != null) {
            if (resultCode == Activity.RESULT_OK) {
                return Uri.fromFile(new File(FileUtil.getFaceSaveFile(host), "camera.jpg"));
            }
        }
        return null;
    }

    /**
     * 开启剪裁
     */
    public static void startCrop(@NonNull Fragment host, Uri src) {
        if (src != null) {
            final Context cxt = host.getContext();
            final File faceFile = new File(FileUtil.getFaceSaveFile(cxt), "face.jpg");
            if (faceFile.exists()) {
                faceFile.delete();
            }
            Crop.of(src, Uri.fromFile(faceFile)).withMaxSize(ASPECT_X, ASPECT_Y).withAspect(3, 4).withTips(R.string.moduleface_string_crop_tips).start(cxt, host);
        }
    }

    /**
     * 获取剪裁返回结果
     */
    @Nullable
    public static Uri getCropResult(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            return Crop.getOutput(result);
        }
        return null;
    }

    /**
     * 弹起选择框
     */
    public static void showActionSheet(final @NonNull Fragment current) {
        final FragmentActivity host = current.getActivity();
        if (host == null) {
            return;
        }
        new ActionSheet().onActionListener(new ActionSheet.OnActionSheetListener() {
            @Override
            public void onChoose(int action) {
                if (action == ActionSheet.PhotoAction.GALLERY) {
                    FaceHelper.startGallery(current);
                } else if (action == ActionSheet.PhotoAction.TAKE_PACTURE) {
                    FaceHelper.startCamera(current);
                }
            }
        }).show(host.getSupportFragmentManager(), ActionSheet.KEY);
    }

    public static Bitmap faceUriToBitmap(@NonNull Uri uri) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(uri.getPath(), options);
    }

    public static Bitmap get2InchesPhoto(@NonNull Bitmap src) {
        return getFixedSizePhoto(src, FaceHelper.ASPECT_X, FaceHelper.ASPECT_Y);
    }

    public static Bitmap getFixedSizePhoto(@NonNull Bitmap src, int width, int height) {
        final int srcWidth = src.getWidth();
        final int srcHeight = src.getHeight();
        if (width != srcWidth || height != srcHeight) {
            final Bitmap scaleBitmap = Bitmap.createScaledBitmap(src, width, height, true);
            if (src != scaleBitmap) {
                src.recycle();
            }
            return scaleBitmap;
        }
        return src;
    }
}
