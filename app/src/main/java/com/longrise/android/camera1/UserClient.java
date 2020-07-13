package com.longrise.android.camera1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godliness on 2020-07-13.
 *
 * @author godliness
 */
public final class UserClient implements ServiceConnection, Handler.Callback {

    private Messenger mClient;
    private Messenger mReceive;
    private Handler mReceiveHandler;

    private List<Bundle> mWaitQueue;
    private boolean mConnectionStatus;

    private static UserClient sUserClient;

    public static UserClient getClient() {
        if (sUserClient == null) {
            synchronized (UserClient.class) {
                if (sUserClient == null) {
                    sUserClient = new UserClient();
                }
            }
        }
        return sUserClient;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        initConnected(service);
        seedAllFromWaitQueue();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        disConnected();
    }

    @Override
    public boolean handleMessage(Message msg) {
        final Bundle extra = msg.getData();
        return true;
    }

    public void sendMessage(Bundle extra, Activity host) {
        if (!mConnectionStatus) {
            bindRemoteService(host);
        }
        if (mClient != null) {
            sendToRemote(extra, false);
        } else {
            addWaitQueue(extra);
        }
    }

    private void sendToRemote(Bundle extra, boolean reply) {
        final Message msg = createMessage(extra, reply);
        try {
            mClient.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(Bundle extra, boolean reply) {
        final Message msg = Message.obtain();
        msg.replyTo = reply ? mReceive : null;
        msg.setData(extra);
        return msg;
    }

    private void bindRemoteService(Activity host) {
        Intent intent = new Intent();
        ComponentName name = new ComponentName("com.longrise.android.camera1", "com.longrise.android.camera1.MessengerService");
        intent.setComponent(name);
        host.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void createExtras() {
        if (mWaitQueue == null) {
            mWaitQueue = new ArrayList<>(5);
        }
    }

    private void addWaitQueue(Bundle extra) {
        if (mWaitQueue == null) {
            createExtras();
        }
        mWaitQueue.add(extra);
    }

    private void seedAllFromWaitQueue() {
        for (Bundle extra : mWaitQueue) {
            sendToRemote(extra, false);
        }
    }

    private UserClient() {
        this.mReceiveHandler = new Handler(this);
        this.mReceive = new Messenger(mReceiveHandler);
    }

    private void initConnected(IBinder service) {
        this.mConnectionStatus = true;
        this.mClient = new Messenger(service);
        this.mReceiveHandler = new Handler(this);
        this.mReceive = new Messenger(mReceiveHandler);
    }

    private void disConnected() {
        this.mConnectionStatus = false;
        this.mClient = null;
        this.mReceive = null;
        this.mReceiveHandler = null;
        if (mWaitQueue != null) {
            mWaitQueue.clear();
        }
    }
}
