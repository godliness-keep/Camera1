package com.longrise.android.camera1;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by godliness on 2020-07-13.
 *
 * @author godliness
 *
 *          <service
 *             android:name=".MessengerService"
 *             android:exported="true"/>
 */
public final class MessengerService extends Service implements Handler.Callback {

    private static final String TAG = "MessengerService";

    private Messenger mReceive;
    private Handler mReceiveHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        createReceive();
    }

    private void createReceive() {
        mReceiveHandler = new Handler(this);
        mReceive = new Messenger(mReceiveHandler);
    }

    @Override
    public boolean handleMessage(Message msg) {
        final Bundle extra = msg.getData();
        if (extra != null) {
            parseFromRemote(extra);
        }
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mReceive.getBinder();
    }

    /**
     * 在这里解析远程发来的数据
     */
    private void parseFromRemote(Bundle extra) {
        final String value = extra.getString("godliness");
        Log.e(TAG, "value: " + value);
    }
}
