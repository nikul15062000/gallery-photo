package com.example.mygallery.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {
    public static boolean getConnectivityStatus(Context context) {
        NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == 1 && activeNetwork.isConnectedOrConnecting()) {
                return true;
            }
            if (activeNetwork.getType() == 0 && activeNetwork.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

}
