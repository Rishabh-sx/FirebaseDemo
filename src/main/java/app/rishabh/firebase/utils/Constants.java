package app.rishabh.firebase.utils;

import android.os.Environment;

/**
 * Created by Rishabh Saxena on 29-04-2017.
 */

public class Constants {

    public static final String APP_IMAGE_FOLDER = Environment.getExternalStorageDirectory().toString() + "/Rastero/images";
    public static final String USER = "USER";
    public static final String INSTITUTE = "INSTITUTE";
    public static final String DRIVER_MAPPER = "DRIVER_MAPPER";
    public static final String INSTITUTE_MAPPER = "INSTITUTE_MAPPER";
    public static final String UNVERIFIED = "UNVERIFIED";

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String MOBILE_PATTERN = "[0-9]+";

}
