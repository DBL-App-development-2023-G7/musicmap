package com.groupseven.musicmap.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;

import com.groupseven.musicmap.util.Constants;

public class NetworkChangeListener extends ConnectivityManager.NetworkCallback {

    private boolean isConnected;
    private final Context context;

    public NetworkChangeListener(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        isConnected = true;
        sendInternetBroadcastWithIsConnected();
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        isConnected = false;
        sendInternetBroadcastWithIsConnected();
    }

    private void sendInternetBroadcastWithIsConnected() {
        Intent broadcastIntent = new Intent(Constants.INTERNET_BROADCAST_ACTION);
        broadcastIntent.putExtra(Constants.INTERNET_BROADCAST_BUNDLE_KEY, isConnected);
        context.sendBroadcast(broadcastIntent);
    }

    public boolean isConnected() {
        return isConnected;
    }

}
