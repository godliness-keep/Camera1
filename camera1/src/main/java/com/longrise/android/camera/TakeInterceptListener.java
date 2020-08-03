package com.longrise.android.camera;

/**
 * Created by godliness on 2020-07-21.
 *
 * @author godliness
 * 业务拦截器
 * 1、拍照拦截
 * 2、暂无
 */
public interface TakeInterceptListener {

    /**
     * 是否拦截拍照， true 表示拦截
     */
    boolean interceptTakePicture();
}
