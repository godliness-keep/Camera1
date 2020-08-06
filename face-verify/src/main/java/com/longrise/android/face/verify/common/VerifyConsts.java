package com.longrise.android.face.verify.common;

/**
 * Created by godliness on 2020-07-07.
 *
 * @author godliness
 */
public final class VerifyConsts {

    public interface RequestCode {
        int GALLERY = 103;
        int TAKE_PICTURE = 104;
    }

    public interface ResultCode {

    }

    public interface PhotoAction {

    }

    public static final int REQUEST_VERIFY_CODE = 101;
    public static final String RESULT_VERIFY_STATUS = "result_verify_status";
    public static final String RESULT_SWITCH_STATUS = "result_switch_status";

    public static final String EXTRA_PREVIEW_PATH = "extra_preview_path";
    public static final String EXTRA_PREVIEW_NAME = "extra_preview_name";
}
