package com.example.donona.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    // Method to check if Wi-Fi is connected
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for null to avoid NullPointerException
        if (connectivityManager != null) {
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            // Check if Wi-Fi is connected
            if (wifiInfo != null && wifiInfo.isConnected()) {
                return true; // Wi-Fi is connected
            }
        }

        return false; // Not connected to Wi-Fi
    }
}

