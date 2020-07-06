package com.longrise.android.camera.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by godliness on 2020-07-05.
 *
 * @author godliness
 */
public final class DpUtil {

    public static int dip2px(Context cxt, float dp) {
        final float scale = cxt.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int getStatusBarHeight(Context context) {
        final Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}
