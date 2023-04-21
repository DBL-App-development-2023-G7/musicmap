package com.groupseven.musicmap.util.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import com.groupseven.musicmap.util.Constants;

/**
 * An utils class for Network related capabilities of our app.
 */
public class NetworkUtils {

    /**
     * Checks if the device is connected to the internet.
     *
     * @param context The context of the application.
     * @return true if the device is connected to the internet, false otherwise.
     */
    public static boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(
                    connectivityManager.getActiveNetwork());
            return capabilities != null
                    && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }

        return false;
    }

    /**
     * Sends a broadcast to other components of the application indicating the current internet
     * connectivity status.
     *
     * @param context The context of the application.
     * @param isConnected The boolean value indicating if internet is available or not.
     */
    public static void sendInternetBroadcast(Context context, boolean isConnected) {
        Intent broadcastIntent = new Intent(Constants.INTERNET_BROADCAST_ACTION);
        broadcastIntent.putExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, isConnected);
        context.sendBroadcast(broadcastIntent);
    }

}
