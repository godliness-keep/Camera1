package com.longrise.android.camera;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.longrise.android.camera.base.BaseBuilder;
import com.longrise.android.camera.base.BaseFragment;

/**
 * Created by godliness on 2020-08-05.
 *
 * @author godliness
 */
public final class PreviewBuilder extends BaseBuilder<PreviewBuilder, PreviewPreviewProxy> {

    int mTemplate = -1;
    View mTemplateView;

    public PreviewBuilder(AppCompatActivity host) {
        super(host);
    }

    public PreviewBuilder templateResource(@IdRes int resId) {
        if (mTemplateView != null) {
            throw new IllegalStateException("Can only set one");
        }
        this.mTemplate = resId;
        return this;
    }

    public PreviewBuilder templateView(View view) {
        if (mTemplate != -1) {
            throw new IllegalStateException("Can only set one");
        }
        this.mTemplateView = view;
        return this;
    }

    @Override
    protected BaseFragment createPreview() {
        return new PreviewFragment();
    }
}
