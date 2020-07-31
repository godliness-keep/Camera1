package com.longrise.android.camera;

/**
 * Created by godliness on 2020-07-21.
 *
 * @author godliness
 * 业务拦截器
 * 1、拍照拦截
 * 2、暂无
 */
public interface UploadInterceptListener {

    /**
     * 是否拦截拍照加上传（局部拦截） true 表示拦截
     */
    boolean interceptUploadPicture();
}
