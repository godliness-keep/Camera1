package com.longrise.android.face;

import com.longrise.android.face.base.IProxy;

/**
 * Created by godliness on 2020-08-11.
 *
 * @author godliness
 */
public interface PreviewProxy extends IProxy {

    /**
     * 重新预览
     *
     * @param url 新的图片地址
     */
    void restartPreview(String url);

}
