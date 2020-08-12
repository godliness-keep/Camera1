package com.longrise.android.face.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.longrise.android.face.R;


/**
 * Created by godliness on 2020-06-22.
 *
 * @author godliness
 */
public final class PreviewShadeView extends FrameLayout {

    private int mBackgroundColor;
    private float mRadius;
    private float mRadiusX;
    private float mRadiusY;
    private float mOffsetY;

    private Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private Paint mEraserPaint;

    private Bitmap mCompassBitmap;
    private float mCompassX;
    private float mCompassY;

    private final Context mCxt;

    public PreviewShadeView(@NonNull Context context) {
        this(context, null);
    }

    public PreviewShadeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mCxt = context;

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PreviewShadeView);
        this.mBackgroundColor = ta.getColor(R.styleable.PreviewShadeView_shade_background, -1);
        this.mRadius = ta.getDimension(R.styleable.PreviewShadeView_shade_radius, 0);
        this.mRadiusX = ta.getDimension(R.styleable.PreviewShadeView_shade_x, 0);
        this.mRadiusY = ta.getDimension(R.styleable.PreviewShadeView_shade_y, 0);
        this.mOffsetY = ta.getDimension(R.styleable.PreviewShadeView_shade_offsetY, 0F);
        ta.recycle();
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldw, int oldh) {
        super.onSizeChanged(newWidth, newHeight, oldw, oldh);
        initSize(newWidth, newHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mEraserBitmap.eraseColor(Color.TRANSPARENT);

        mEraserCanvas.drawColor(mBackgroundColor);
        mEraserCanvas.drawCircle(mRadiusX, mRadiusY, mRadius, createIfPaint());
        canvas.drawBitmap(mEraserBitmap, 0, 0, null);
        canvas.drawBitmap(mCompassBitmap, mCompassX, mCompassY, null);
    }

    private void initSize(int width, int height) {
//        final int x = cxt.getResources().getDisplayMetrics().widthPixels;
//        final int y = cxt.getResources().getDisplayMetrics().heightPixels;
        final float scale = mCxt.getApplicationContext().getResources().getDisplayMetrics().density;

        if (mRadius <= 0) {
            mRadius = (float) width / 3;
        } else {
            mRadius = dip2px(scale, mRadius);
        }

        if (mRadiusX <= 0) {
            mRadiusX = (float) width / 2;
        } else {
            mRadiusX = dip2px(scale, mRadiusX);
        }
        if (mOffsetY <= 0) {
            mOffsetY = mRadius / 2;
        } else {
            mOffsetY = dip2px(scale, mOffsetY);
        }

        if (mRadiusY <= 0) {
            mRadiusY = (float) height / 2 - mOffsetY;
        } else {
            mRadiusY = dip2px(scale, mRadiusY);
        }

        if (mBackgroundColor <= 0) {
            mBackgroundColor = Color.parseColor("#55FFFFFF");
        }

        createEraserBitmap(width, height);
        initCompassSize(scale);
    }

    private void initCompassSize(float scale) {
        createCompassBitmap();
        mCompassX = mRadiusX - (float) mCompassBitmap.getWidth() / 2;
        mCompassY = mRadiusY - (float) mCompassBitmap.getHeight() / 2 + 30 ;
    }

    private void createEraserBitmap(int x, int y) {
        mEraserBitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);
    }

    private void createCompassBitmap() {
        mCompassBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moduleface_circle_bg_compass);
    }

    private Paint createIfPaint() {
        if (mEraserPaint == null) {
            mEraserPaint = new Paint();
            mEraserPaint.setColor(0XFFFFFFFF);
            mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mEraserPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }
        return mEraserPaint;
    }

    private float dip2px(float scale, float dp) {
        return (int) (dp * scale + 0.5f);
    }
}
