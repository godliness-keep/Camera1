package com.longrise.android.camera;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.longrise.android.camera.base.BaseBuilder;
import com.longrise.android.camera.base.BaseFragment;


/**
 * Created by godliness on 2020-07-04.
 *
 * @author godliness
 */
public final class FaceBuilder extends BaseBuilder<FaceBuilder, FacePreviewProxy> {

    FaceInterceptListener mInterceptListener;
    boolean mTranslucentStatus;

    public FaceBuilder(AppCompatActivity host) {
        super(host);
    }

    /**
     * 业务拦截器
     */
    public FaceBuilder takeInterceptListener(FaceInterceptListener interceptListener) {
        this.mInterceptListener = interceptListener;
        return this;
    }

    /**
     * 沉浸式状态栏
     */
    public FaceBuilder translucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mTranslucentStatus = true;
        }
        return this;
    }

    @Override
    protected BaseFragment createPreview() {
        return new FaceFragment();
    }
}
