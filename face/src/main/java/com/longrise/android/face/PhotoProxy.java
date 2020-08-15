package com.longrise.android.face;

import android.graphics.Bitmap;
import android.support.annotation.StringRes;

import com.longrise.android.face.base.IProxy;

/**
 * Created by godliness on 2020/8/12.
 *
 * @author godliness
 */
public interface PhotoProxy extends IProxy {

    /**
     * 获取当前相片
     *
     * @return Current photo
     */
    Bitmap getCurrentPhoto();

    /**
     * 修改提交按钮文字（最多5个字符）
     */
    void changeCommitStatus(@StringRes int strId);

    /**
     * 控制提交按钮显示状态
     */
    void changeCommitVisible(boolean visible);
}
