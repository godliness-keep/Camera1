package com.longrise.android.camera;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.longrise.android.camera.base.BaseFragment;
import com.longrise.android.camera.preview.CameraPreview;

/**
 * Created by godliness on 2020-08-05.
 *
 * @author godliness
 * 拍照
 */
public final class PreviewFragment extends BaseFragment<PreviewBuilder> implements PreviewPreviewProxy {

    private CameraPreview mPreview;
    private ImageView mTemplate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.modulecamera_fragment_take, container, false);
    }

    @Override
    protected void initView() {
        mPreview = findViewById(R.id.camera_preview);
        mTemplate = findViewById(R.id.iv_template);
    }

    @Override
    protected CameraPreview preview() {
        return mPreview;
    }

    @Override
    protected void configBuilder(PreviewBuilder builder) {
        if(builder.mTemplate != -1){
            mTemplate.setImageResource(builder.mTemplate);
        }else if(builder.mTemplateView != null){
            final FrameLayout fragment = (FrameLayout) getView();
            fragment.addView(builder.mTemplateView);
        }
    }
}
