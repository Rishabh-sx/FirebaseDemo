package app.rishabh.firebase.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rishabh on 13/09/16.
 */
public class NetworkConnectionIndicator {

    private static NetworkConnectionIndicator mInstance;
    private Context app;
    private boolean wifiConnected;
    private boolean mobileNetworkConnected;

    private NetworkConnectionIndicator(Context app) {
        this.app = app;
    }

    public static NetworkConnectionIndicator getInstance(Context app) {
        if (mInstance == null) {
            mInstance = new NetworkConnectionIndicator(app);
        }
        mInstance.refreshConnectionState();
        return mInstance;
    }

    public void refreshConnectionState() {
        mobileNetworkConnected = false;
        wifiConnected = false;
        checkMobileNetworkState();
        checkWifiNetworkState();
    }

    private void checkMobileNetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager)app
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            mobileNetworkConnected = true;
            wifiConnected = false;
        }
    }

    private void checkWifiNetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager)app
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
                && activeNetInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            wifiConnected = true;
            mobileNetworkConnected = false;
        }
    }

    /**
     * Get the network state
     *
     * @param refreshNow Pass false if you recently obtained an instance of NetworkUtil by calling
     *            <code>getInstance()</code>. Pass true to refresh connections state.
     * @return true if mobile(data) or wifi is connected.
     */
    public boolean isNetworkAvailable(boolean refreshNow) {
        if (refreshNow) {
            refreshConnectionState();
        }
       /* if(wifiConnected)
        {
            WifiManager wifiManager = (WifiManager)app.getSystemService(Context.WIFI_SERVICE);
            int linkSpeed = wifiManager.getConnectionInfo().getRssi();
            Log.e("wifi speed",linkSpeed+"");

        }else if(mobileNetworkConnected)
        {
            TelephonyManager telephonyManager =        (TelephonyManager)app.getSystemService(Context.TELEPHONY_SERVICE);
            CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
            CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
            cellSignalStrengthGsm.getDbm();
        }*/
        return wifiConnected || mobileNetworkConnected;
    }
}
