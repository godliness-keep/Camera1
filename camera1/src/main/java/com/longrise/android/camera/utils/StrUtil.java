package com.longrise.android.camera.utils;

/**
 * Created by godliness on 2020-08-02.
 *
 * @author godliness
 */
public final class StrUtil {

    public static String arrayToString(String... msg) {
        final int length;
        if (msg == null || (length = msg.length) <= 0) {
            return null;
        }
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++) {
            b.append(msg[i]);
        }
        return b.toString();
    }

    private StrUtil() {
    }
}
