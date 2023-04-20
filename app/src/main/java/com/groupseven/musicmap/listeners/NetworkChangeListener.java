package com.groupseven.musicmap.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;

import com.groupseven.musicmap.util.Constants;

/**
 * Responsible for monitoring the network connectivity changes and broadcasting
 * the internet connectivity status to other components of the application.
 */
public class NetworkChangeListener extends ConnectivityManager.NetworkCallback {

    /**
     * The application context used to send broadcasts to other components of the application.
     */
    private final Context context;

    /**
     * Indicates whether the device is currently connected to the internet or not.
     */
    private boolean isConnected;

    /**
     * Creates a new instance of the NetworkChangeListener class with the specified context.
     *
     * @param context the context used to send broadcasts to other components of the application.
     */
    public NetworkChangeListener(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * This method is called when a network connection is established and sets the
     * {@link NetworkChangeListener#isConnected} field to {@code true}. It also sends a broadcast to notify
     * other components of the application that the internet connection is available.
     *
     * @param network the network that became available.
     */
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        isConnected = true;
        sendInternetBroadcastWithIsConnected();
    }

    /**
     * This method is called when a network connection is established and sets the
     * {@link NetworkChangeListener#isConnected} field to {@code false}. It also sends a broadcast to notify
     * other components of the application that the internet connection is lost.
     *
     * @param network the network that became available.
     */
    @Override
    public void onLost(Network network) {
        super.onLost(network);
        isConnected = false;
        sendInternetBroadcastWithIsConnected();
    }

    /**
     * Sends a broadcast to other components of the application indicating the current internet
     * connectivity status.
     */
    private void sendInternetBroadcastWithIsConnected() {
        Intent broadcastIntent = new Intent(Constants.INTERNET_BROADCAST_ACTION);
        broadcastIntent.putExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, isConnected);
        context.sendBroadcast(broadcastIntent);
    }

    /**
     * Returns whether the device is currently connected to the internet or not.
     *
     * @return true if the device is currently connected to the internet, false otherwise.
     */
    public boolean isConnected() {
        return isConnected;
    }

}
