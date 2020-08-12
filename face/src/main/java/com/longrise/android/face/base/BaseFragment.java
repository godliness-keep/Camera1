package com.longrise.android.face.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by godliness on 2020-08-09.
 *
 * @author godliness
 */
public abstract class BaseFragment<Builder extends BaseBuilder> extends Fragment {

    private Builder mBuilder;

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(savedInstanceState), container, false);
    }

    @Override
    public final void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        configBuilder(mBuilder);
        mBuilder = null;
    }

    @SuppressWarnings("unchecked")
    public final <Proxy extends IProxy> Proxy commit(Builder builder) {
        this.mBuilder = builder;
        return (Proxy) this;
    }

    protected abstract int getLayoutResource(Bundle state);

    protected abstract void initView();

    /**
     * Call back after {@link #initView()}
     */
    protected abstract void configBuilder(Builder builder);

    protected final <V extends View> V findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }
}
