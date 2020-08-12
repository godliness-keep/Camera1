package com.longrise.android.face.assist;


import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.longrise.android.face.R;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020-08-10.
 *
 * @author godliness
 */
public final class TipsManager implements Handler.Callback {

    private static final int MSG_HIDE = 0;

    private final Handler mTimeHandler;
    private final TextView mView;
    private final WeakReference<ViewGroup> mHost;

    private static volatile TipsManager sTips;

    public static TipsManager make(ViewGroup host, String msg) {
        return create(host, msg);
    }

    public static TipsManager make(ViewGroup host, @StringRes int msgId) {
        return create(host, host.getContext().getString(msgId));
    }

    private static TipsManager create(ViewGroup host, String msg) {
        if (sTips != null) {
            return sTips.setText(msg);
        }
        return sTips = new TipsManager(host, msg);
    }

    public void show() {
        attachToHost();
        sendDelayHide();
    }

    @Override
    public boolean handleMessage(Message msg) {
        detachFromHost();
        return true;
    }

    TipsManager setText(String msg) {
        if (mView != null) {
            mView.setText(msg);
        }
        sendDelayHide();
        return this;
    }

    private void sendDelayHide() {
        mTimeHandler.removeMessages(MSG_HIDE);
        mTimeHandler.sendEmptyMessageDelayed(MSG_HIDE, 3500);
    }

    private void attachToHost() {
        final ViewParent parent = mView.getParent();
        if (parent != null) {
            return;
        }
        final ViewGroup content = mHost.get();
        if (content != null) {
            content.addView(mView);
        }
    }

    private void detachFromHost() {
        final ViewParent parent = mView.getParent();
        if (parent instanceof ViewGroup) {
            mView.startAnimation(createHideAnim());
            ((ViewGroup) parent).removeView(mView);
        }
        sTips = null;
    }

    private final View.OnAttachStateChangeListener mViewStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View view) {
            view.startAnimation(createShowAnim());
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            v.removeOnAttachStateChangeListener(mViewStateChangeListener);
        }
    };

    private Animation createShowAnim() {
        final TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        animation.setDuration(800);
        return animation;
    }

    private Animation createHideAnim() {
        final TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 1.0f
        );
        animation.setDuration(1000);
        return animation;
    }

    private TipsManager(ViewGroup host, String msg) {
        this.mHost = new WeakReference<>(host);
        this.mTimeHandler = new Handler(this);
        this.mView = (TextView) LayoutInflater.from(host.getContext()).inflate(R.layout.moduleface_layout_tips, host, false);
        mView.addOnAttachStateChangeListener(mViewStateChangeListener);
        mView.setText(msg);
    }
}
