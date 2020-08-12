package com.longrise.android.camera.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.longrise.android.camera.utils.DpUtil;
import com.longrise.android.camera.R;


/**
 * Created by godliness on 2020-06-22.
 *
 * @author godliness
 */
public final class PreviewShadeView extends FrameLayout {

    private static final String TAG = "PreviewShadeView";

    private int mBackgroundColor;
    private float mRadius;
    private float mRadiusX;
    private float mRadiusY;
    private float mCircleX;
    private float mCircleY;
    private float mOffsetY;

    private Bitmap mEraserBitmap;
    private Bitmap mCircleBitmap;
    private Canvas mEraserCanvas;
    private Paint mEraserPaint;

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
        this.mCircleX = ta.getDimension(R.styleable.PreviewShadeView_circle_x, 0);
        this.mCircleY = ta.getDimension(R.styleable.PreviewShadeView_circle_y, 0);
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
        canvas.drawBitmap(mCircleBitmap, mCircleX, mCircleY, null);
    }

    private void initSize(int width, int height) {
//        final int x = cxt.getResources().getDisplayMetrics().widthPixels;
//        final int y = cxt.getResources().getDisplayMetrics().heightPixels;
        final Context cxt = mCxt;

        //提前创建 计算绘制位置需要
        createCircleBitmap();

        if (mRadius <= 0) {
            mRadius = width / 3;
        } else {
            mRadius = DpUtil.dip2px(cxt, mRadius);
        }

        if (mRadiusX <= 0) {
            mRadiusX = width / 2;
        } else {
            mRadiusX = DpUtil.dip2px(cxt, mRadiusX);
        }
        if (mOffsetY <= 0F) {
            mOffsetY = mRadius / 2;
        } else {
            mOffsetY = DpUtil.dip2px(cxt, mOffsetY);
        }

        if (mRadiusY <= 0) {
            mRadiusY = height / 2 - mOffsetY;
        } else {
            mRadiusY = DpUtil.dip2px(cxt, mRadiusY);
        }

        if (mCircleX <= 0) {
            if (mCircleBitmap!=null) {
                int bitmapHalfX = mCircleBitmap.getWidth() / 2;
                mCircleX = mRadiusX - bitmapHalfX;
            }
        } else {
            mCircleX = DpUtil.dip2px(cxt, mRadiusY);
        }

        if (mCircleY <= 0) {
            if (mCircleBitmap!=null) {
                int bitmapHalfY = mCircleBitmap.getHeight() / 2;
                mCircleY = mRadiusY - bitmapHalfY + DpUtil.dip2px(mCxt, 10);
            }
        } else {
            mCircleY = DpUtil.dip2px(cxt, mRadiusY);
        }

        if (mBackgroundColor <= 0) {
            mBackgroundColor = Color.parseColor("#55FFFFFF");
        }

        createEraserBitmap(width, height);
    }

    private void createEraserBitmap(int x, int y) {
        mEraserBitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);
    }

    private void createCircleBitmap() {
        mCircleBitmap = BitmapFactory.decodeResource(getContext().getResources()
                , R.drawable.moduleface_circle_bg_round);
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
}
