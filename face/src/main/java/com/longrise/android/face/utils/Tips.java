package com.longrise.android.face.utils;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;

import com.longrise.android.face.BuildConfig;
import com.longrise.android.face.assist.TipsManager;

/**
 * Created by godliness on 2020-08-09.
 *
 * @author godliness
 */
public final class Tips {

    public static void showTips(Activity host, @StringRes int msgId) {
        showTips(host, host.getString(msgId));
    }

    public static void showTips(Activity host, String msg) {
        final ViewGroup content = host.findViewById(Window.ID_ANDROID_CONTENT);
        TipsManager.make(content, msg).show();
    }

    public static void showTips(Fragment host, String msg) {
        final Activity parent = host.getActivity();
        if (parent != null) {
            showTips(parent, msg);
        }
    }

    public static void showTips(Fragment host, @StringRes int msgId) {
        showTips(host, host.getString(msgId));
    }

    public static void log(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

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

    private Tips() {
    }
}
