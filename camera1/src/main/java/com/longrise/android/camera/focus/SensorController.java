package com.longrise.android.camera.focus;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Calendar;

/**
 * Created by godliness on 2020-07-06.
 *
 * @author godliness
 * 自动对焦控制
 */
public final class SensorController implements FocusActivityLifiCycle, SensorEventListener {

    private static final int STATUS_NONE = 0;
    private static final int STATUS_STATIC = 1;
    private static final int STATUS_MOVE = 2;
    private int STATUE = STATUS_NONE;

    private static final int DELAY_DURATION = 100;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private int mX, mY, mZ;
    private long mLastStaticStamp = 0;

    private boolean mFocusing;
    private boolean mCanFocusIn;
    private boolean mCanFocus;
    private int mFocusStatus = 1;

    private CameraFocusListener mCameraFocusListener;

    public interface CameraFocusListener {
        /**
         * 触发对焦
         */
        void onFocus();
    }

    public SensorController(Context cxt) {
        this.mSensorManager = (SensorManager) cxt.getApplicationContext().getSystemService(Activity.SENSOR_SERVICE);
        this.mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setCameraFocusListener(CameraFocusListener mCameraFocusListener) {
        this.mCameraFocusListener = mCameraFocusListener;
    }

    @Override
    public void onStart() {
        if (!mCanFocus) {
            mCanFocus = true;
            resetParams();
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onStop() {
        if (mCanFocus) {
            mCanFocus = false;
            mSensorManager.unregisterListener(this, mSensor);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }

        if (mFocusing) {
            resetParams();
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float[] values = event.values;
            final int x = (int) values[0];
            final int y = (int) values[1];
            final int z = (int) values[2];

            final long stamp = Calendar.getInstance().getTimeInMillis();

            if (STATUE != STATUS_NONE) {
                final int px = Math.abs(mX - x);
                final int py = Math.abs(mY - y);
                final int pz = Math.abs(mZ - z);
                final double value = Math.sqrt(px * px + py * py + pz * pz);
                if (value > 1.4) {
                    STATUE = STATUS_MOVE;
                } else {
                    if (STATUE == STATUS_MOVE) {
                        mLastStaticStamp = stamp;
                        mCanFocusIn = true;
                    }

                    if (mCanFocusIn) {
                        if (stamp - mLastStaticStamp > DELAY_DURATION) {
                            if (!mFocusing) {
                                mCanFocusIn = false;
                                if (mCameraFocusListener != null) {
                                    mCameraFocusListener.onFocus();
                                }
                            }
                        }
                    }
                    STATUE = STATUS_STATIC;
                }
            } else {
                mLastStaticStamp = stamp;
                STATUE = STATUS_STATIC;
            }

            mX = x;
            mY = y;
            mZ = z;
        }
    }

    /**
     * 对焦是否被锁定
     */
    public boolean isFocusLocked() {
        if (mCanFocus) {
            return mFocusStatus <= 0;
        }
        return false;
    }

    /**
     * 锁定对焦
     */
    public void lockFocus() {
        mFocusing = true;
        mFocusStatus--;
    }

    /**
     * 解锁对焦
     */
    public void unlockFocus() {
        mFocusing = false;
        mFocusStatus++;
    }

    /**
     * 重置焦点
     */
    public void resetFocus() {
        mFocusStatus = 1;
    }

    private void resetParams() {
        STATUE = STATUS_NONE;
        mCanFocusIn = false;
        mX = 0;
        mY = 0;
        mZ = 0;
    }

}
