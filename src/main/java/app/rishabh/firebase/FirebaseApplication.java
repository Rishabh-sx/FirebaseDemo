package app.rishabh.firebase;

import android.app.Application;

import app.rishabh.firebase.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Rishabh Saxena
 * rishabh.saxena@appinventiv.com
 * Appinventiv Technologies Pvt. Ltd.
 * on 29/3/17.
 */
public class FirebaseApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ubuntu-Light_0.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}
