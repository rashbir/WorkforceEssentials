package com.rash.workforceessentials.libraries;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    // Interface to listen for network connectivity changes
    public interface ConnectivityChangeListener {
        void onNetworkAvailable();
        void onNetworkUnavailable();
    }

    // Function to check if the device is connected to the internet
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            // For devices below Android M
            return cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnected();
        }
    }

    // Function to register a network connectivity change listener
    public static void registerConnectivityChangeListener(Context context,
                                                          final ConnectivityChangeListener listener) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            Log.e(TAG, "ConnectivityManager is null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    listener.onNetworkAvailable();
                }

                @Override
                public void onLost(Network network) {
                    listener.onNetworkUnavailable();
                }
            });
        }
    }
}

