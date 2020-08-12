package com.longrise.android.face.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * Created by godliness on 2020-08-08.
 *
 * @author godliness
 */
public abstract class BaseBuilder<Proxy extends IProxy> {

    private static final String COMMIT_KEY = "fragment_commit_key";

    private AppCompatActivity mHost;

    public BaseBuilder(AppCompatActivity host) {
        this.mHost = host;
    }

    public Proxy commitAndSaveState(@Nullable Bundle state) {
        return commitAndSaveState(state, Window.ID_ANDROID_CONTENT);
    }

    @SuppressWarnings("unchecked")
    public Proxy commitAndSaveState(@Nullable Bundle state, @IdRes int contentId) {
        final BaseFragment proxy;
        if (state == null) {
            proxy = commitPreview(contentId);
        } else {
            proxy = restorePreview();
        }
        return (Proxy) proxy.commit(this);
    }

    protected abstract Fragment createPreview();

    protected Bundle getExtra() {
        return null;
    }

    private FragmentManager getFragmentManager() {
        return mHost.getSupportFragmentManager();
    }

    private BaseFragment restorePreview() {
        return (BaseFragment) getFragmentManager().findFragmentByTag(COMMIT_KEY);
    }

    private BaseFragment commitPreview(int contentId) {
        final Fragment previewProxy = createPreview();
        previewProxy.setArguments(getExtra());
        getFragmentManager().beginTransaction().replace(contentId, previewProxy, COMMIT_KEY).commit();
        return (BaseFragment) previewProxy;
    }
}
