package com.longrise.android.face.assist;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.longrise.android.face.R;


/**
 * Created by godliness on 2020-08-06.
 *
 * @author godliness
 */
public final class ActionSheet extends DialogFragment implements View.OnClickListener {

    public static final String KEY = "action-sheet";

    public interface PhotoAction {
        int TAKE_PACTURE = 0;
        int GALLERY = 1;
    }

    public interface OnActionSheetListener {

        void onChoose(int action);

    }

    private OnActionSheetListener mActionListener;

    public ActionSheet() {

    }

    public static ActionSheet create() {
        return new ActionSheet();
    }

    public ActionSheet onActionListener(OnActionSheetListener actionSheetListener) {
        this.mActionListener = actionSheetListener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.moduleface_dialog_action_sheet, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View takePicture = getView().findViewById(R.id.tv_take_picture);
        takePicture.setOnClickListener(this);
        final View fromGallery = getView().findViewById(R.id.tv_from_gallery);
        fromGallery.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        final Window window = dialog.getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return dialog;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        final int id = v.getId();
        if (id == R.id.tv_take_picture) {
            mActionListener.onChoose(PhotoAction.TAKE_PACTURE);
        } else if (id == R.id.tv_from_gallery) {
            mActionListener.onChoose(PhotoAction.GALLERY);
        }
    }
}
