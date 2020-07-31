package com.longrise.android.camera.assist;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by YZH on 2020/7/31.
 * From the BaoBao project
 *
 * @author YZH
 */
public class TimerAssist {

    public static final String TAG = TimerAssist.class.getSimpleName();
    /**
     * 首先申明一个对象变量，变量都是必须私有化
     */
    private static TimerAssist timerAssist = null;

    /**
     * 秒
     */
    private final int TIME_SECOND = 1000;

    /**
     * 30秒
     */
    private final int TIME_TOTAL = 30 * 1000 + 1000;

    /**
     * 倒计时
     */
    private UploadTimer uploadTimer = null;

    /**
     * 是否拦截上传
     */
    private boolean isIntercept = false;

    /**
     * 匹配id
     */
    private String faceMatchId;

    public String getFaceMatchId() {
        return faceMatchId;
    }

    public void setFaceMatchId(String faceMatchId) {
        this.faceMatchId = faceMatchId;
    }


    public static TimerAssist getInstance() {

        if (null == timerAssist) {
            timerAssist = new TimerAssist();
        }
        return timerAssist;

    }

    public boolean getIsIntercept(){
        return isIntercept;
    }

    /**
     * start Timer
     */
    public synchronized void startPlayerTimer(String faceMatchId) {
        stopPlayerTimer();
        if (uploadTimer == null) {
            uploadTimer = new UploadTimer(TIME_TOTAL, TIME_SECOND);
            uploadTimer.start();
            isIntercept = true;
            setFaceMatchId(faceMatchId);
        }
    }

    /**
     * stop Timer
     */
    public synchronized void stopPlayerTimer() {
        try {
            if (uploadTimer != null) {
                uploadTimer.cancel();
                uploadTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * release
     */
    public void timerRelease() {
        isIntercept = false;
        setFaceMatchId("");
        stopPlayerTimer();
        timerAssist = null;
    }

    public class UploadTimer extends CountDownTimer {

        public UploadTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long leftTime = millisUntilFinished / 1000;
            if (leftTime>0) {
                isIntercept = true;
            }
        }

        @Override
        public void onFinish() {
            timerRelease();
        }
    }
} 