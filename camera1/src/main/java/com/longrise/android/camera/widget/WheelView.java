package com.longrise.android.camera.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.longrise.android.camera.R;
import com.longrise.android.camera.utils.DpUtil;


/**
 * Created by godliness on 2020-06-22.
 *
 * @author godliness
 */
public final class WheelView extends View {

    private static final String TAG = "WheelView";

    private float mRadiusX;
    private float mRadiusY;
    private float mOffsetY;
    private float mRadius;
    private int mStartColor;
    private int mEndColor;

    private Paint mWheelPaint;
    private Paint mWheelPaint1;

    private RectF mRectF;
    private LinearGradient mGradient;
    private LinearGradient mGradient1;
    private Animation mRotateAnimation;

    private final Context mCxt;
    private boolean mRotating;

    public void startRotate() {
        if (!mRotating) {
            stopRotate();
            startAnimation(createIfRotateAnimator());
            mRotating = true;
        }
    }

    public void stopRotate() {
        if (mRotating) {
            if (mRotateAnimation != null) {
                mRotateAnimation.cancel();
                mRotating = false;
            }
        }
    }

    public boolean isRotating() {
        return mRotating;
    }

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mCxt = context;

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        this.mRadius = ta.getDimension(R.styleable.WheelView_wheel_radius, 0F);
        this.mRadiusX = ta.getDimension(R.styleable.WheelView_wheel_x, 0F);
        this.mRadiusY = ta.getDimension(R.styleable.WheelView_wheel_y, 0F);
        this.mOffsetY = ta.getDimension(R.styleable.WheelView_wheel_offsetY, 0F);
        this.mStartColor = ta.getColor(R.styleable.WheelView_wheel_start_color, Color.parseColor("#00FFA01A"));
        this.mEndColor = ta.getColor(R.styleable.WheelView_wheel_end_color, Color.parseColor("#FF9600"));
        ta.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSise(w, h);
    }

    private void initSise(int width, int height) {
//        final int x = context.getResources().getDisplayMetrics().widthPixels;
//        final int y = context.getResources().getDisplayMetrics().heightPixels;
        final Context cxt = this.mCxt;

        if (mRadius <= 0F) {
            mRadius = width / 3;
        } else {
            mRadius = DpUtil.dip2px(cxt, mRadius);
        }

        if (mRadiusX <= 0F) {
            mRadiusX = width / 2;
        } else {
            mRadiusX = DpUtil.dip2px(cxt, mRadiusX);
        }
        if (mOffsetY <= 0F) {
            mOffsetY = mRadius / 2;
        } else {
            mOffsetY = DpUtil.dip2px(cxt, mOffsetY);
        }
        if (mRadiusY <= 0F) {
            mRadiusY = height / 2 - mOffsetY;
        } else {
            mRadiusY = DpUtil.dip2px(cxt, mRadiusY);
        }

        mRadius += DpUtil.dip2px(cxt, 16);

        createWheelPaint();
        createWheelPaint1();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(
                createIfRectF(),
                -180,
                90,
                false,
                mWheelPaint);

        canvas.drawArc(
                createIfRectF(),
                0,
                90,
                false,
                mWheelPaint1);
    }

    private void createWheelPaint() {
        mWheelPaint = new Paint();
        mWheelPaint.setStrokeWidth(16);
        mWheelPaint.setStrokeCap(Paint.Cap.ROUND);
        mWheelPaint.setStyle(Paint.Style.STROKE);
        mWheelPaint.setAntiAlias(true);
        mWheelPaint.setFilterBitmap(true);

        mWheelPaint.setShader(createIfGradient());
    }

    private void createWheelPaint1() {
        mWheelPaint1 = new Paint();
        mWheelPaint1.setStrokeWidth(16);
        mWheelPaint1.setStrokeCap(Paint.Cap.ROUND);
        mWheelPaint1.setStyle(Paint.Style.STROKE);
        mWheelPaint1.setAntiAlias(true);
        mWheelPaint1.setFilterBitmap(true);

        mWheelPaint1.setShader(createIfGradient1());
    }

    private LinearGradient createIfGradient() {
        if (mGradient == null) {
            mGradient = new LinearGradient(
                    mRadiusX - mRadius,
                    mRadiusY,
                    mRadiusX,
                    mRadiusY - mRadius,
                    new int[]{mStartColor, mEndColor},
                    null,
                    Shader.TileMode.MIRROR
            );
        }
        return mGradient;
    }

    private LinearGradient createIfGradient1() {
        if (mGradient1 == null) {
            mGradient1 = new LinearGradient(
                    mRadiusX - mRadius,
                    mRadiusY,
                    mRadiusX,
                    mRadiusY - mRadius,
                    new int[]{mStartColor, mEndColor},
                    null,
                    Shader.TileMode.MIRROR
            );
        }
        return mGradient1;
    }

    private RectF createIfRectF() {
        if (mRectF == null) {
            mRectF = new RectF(
                    mRadiusX - mRadius,
                    mRadiusY - mRadius,
                    mRadiusX + mRadius,
                    mRadiusY + mRadius);
        }
        return mRectF;
    }

    private Animation createIfRotateAnimator() {
        if (mRotateAnimation == null) {
            mRotateAnimation = new RotateAnimation(0, 360, mRadiusX, mRadiusY);
            mRotateAnimation.setDuration(3000);
            mRotateAnimation.setInterpolator(new LinearInterpolator());
            mRotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        }
        return mRotateAnimation;
    }
}
