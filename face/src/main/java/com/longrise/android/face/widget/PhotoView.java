package com.longrise.android.face.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by godliness on 2020-08-09.
 *
 * @author godliness
 */
public final class PhotoView extends AppCompatImageView {

    private Bitmap mCurrent;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if(mCurrent != null && mCurrent != bm){
            mCurrent.recycle();
        }
        this.mCurrent = bm;
    }

    public Bitmap getImageBitmap() {
        return mCurrent;
    }
}
