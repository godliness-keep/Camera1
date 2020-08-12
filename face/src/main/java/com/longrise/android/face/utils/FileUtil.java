package com.longrise.android.face.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by godliness on 2020-08-07.
 *
 * @author godliness
 */
public final class FileUtil {

    public static boolean checkSDCard() {
        return TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);
    }

    @NonNull
    public static File getFaceSaveFile(Context cxt) {
        if (checkSDCard()) {
            final File file = cxt.getExternalFilesDir("face");
            if (file != null) {
                return file;
            }
        }
        return new File(cxt.getFilesDir(), "face");
    }

    private FileUtil() {

    }
}

