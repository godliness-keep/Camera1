package com.longrise.android.face.verify;

import android.content.Intent;

/**
 * Created by godliness on 2020-08-05.
 *
 * @author godliness
 */
public interface FaceVerifyListener {

    boolean onFaceVerifyResult(int requestCode, Intent data);

}
