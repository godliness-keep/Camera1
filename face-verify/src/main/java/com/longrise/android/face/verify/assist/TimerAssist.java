package com.longrise.android.face.verify.assist;

import android.os.CountDownTimer;

import com.longrise.android.face.verify.utils.StrUtil;

/**
 * Created by YZH on 2020/7/31.
 * From the BaoBao project
 *
 * @author YZH
 * <p>
 * todo 上传成功后之后，30s 以内都应该去查询，条件是未发生用户切换（暂时未关联用户）
 * <p>
 * Service 机制：
 * 1、 客户端消费掉之后即失效，否则即便是匹配失败的也一直被复用
 * 2、 超时 30s 即销毁
 * <p>
 * 3、如果正在排队，发生再次上传图片怎么办？替换
 * <p>
 * Client:
 * 1、如果未消费查询结果 复用本次查询结果
 * 2、如果未查询，直接查询本次匹配结果
 * 2、超时 30s 即失效
 */
public class TimerAssist extends CountDownTimer {

    private static volatile TimerAssist sTimer;

    private boolean mReuseIntercept;
    private String mCurrentMatchId;
    private FaceMatchResult mFaceMatchResult;

    /**
     * 上传成功之后创建复用管理器
     */
    public static void createPlayerTimer(String id) {
        createPlayerTimer(30 * 1000 + 1000, 1000, id);
    }

    /**
     * 上传成功之后创建复用管理器
     */
    public static void createPlayerTimer(long millisInFuture, long countDownInterval, String matchId) {
        if (sTimer == null) {
            synchronized (TimerAssist.class) {
                if (sTimer == null) {
                    sTimer = new TimerAssist(millisInFuture, countDownInterval);
                    sTimer.bindFaceMatchId(matchId);
                }
            }
        }
    }

    /**
     * 停止复用计数器
     */
    public static void stopPlayerTimer() {
        if (sTimer != null) {
            sTimer.stopInner();
        }
    }

    /**
     * 暂存匹配结果
     */
    public static void stagingMatchResult(boolean matchResult, String... desc) {
        if (sTimer != null) {
            sTimer.createReuseResult(matchResult, desc);
        }
    }

    /**
     * 是否存在未消费的查询结果
     */
    public static FaceMatchResult hasMatchResult() {
        if (sTimer != null) {
            return sTimer.mFaceMatchResult;
        }
        return null;
    }

    /**
     * 是否需要拦截拍照上传
     */
    public static boolean isIntercept() {
        if (sTimer != null) {
            return sTimer.mReuseIntercept;
        }
        return false;
    }

    /**
     * 获取对应上传服务的照片 Id
     */
    public static String getFaceMatchId() {
        if (sTimer != null) {
            return sTimer.mCurrentMatchId;
        }
        throw new IllegalStateException("TimerAssist status exception");
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // do nothing
    }

    @Override
    public void onFinish() {
        stopInner();
    }

    public static final class FaceMatchResult {

        private final TimerAssist mAssist;

        private boolean mMatchResult;
        private String[] mDesc;

        FaceMatchResult(TimerAssist assist) {
            this.mAssist = assist;
        }

        void matchResult(boolean matchResult) {
            this.mMatchResult = matchResult;
        }

        void desc(String... desc) {
            this.mDesc = desc;
        }

        public boolean matchResult() {
            return mMatchResult;
        }

        public String desc() {
            return StrUtil.arrayToString(mDesc);
        }

        public void recycle() {
            mAssist.stopInner();
        }
    }

    private void bindFaceMatchId(String matchId) {
        this.mCurrentMatchId = matchId;
    }

    private void stopInner() {
        cancel();
        mReuseIntercept = false;
        mFaceMatchResult = null;
        mCurrentMatchId = null;
        sTimer = null;
    }

    private void createUploadTimer() {
        mReuseIntercept = true;
        start();
    }

    private TimerAssist(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        createUploadTimer();
    }

    private void createReuseResult(boolean result, String... desc) {
        final FaceMatchResult matchResult = new FaceMatchResult(this);
        matchResult.matchResult(result);
        matchResult.desc(desc);
        mFaceMatchResult = matchResult;
    }
}