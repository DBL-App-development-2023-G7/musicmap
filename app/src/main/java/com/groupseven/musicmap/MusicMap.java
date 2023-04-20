package com.groupseven.musicmap;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;

import com.groupseven.musicmap.listeners.NetworkChangeListener;

/**
 * The main Application.
 */
public class MusicMap extends Application {

    /**
     * This is the application instance.
     */
    private static MusicMap instance;

    /**
     * This is instance for the app resources.
     */
    private static Resources resources;

    /**
     * The {@link NetworkChangeListener} object to register internet check throughout the app.
     */
    private NetworkChangeListener networkChangeListener;

    /**
     * Gets the instance of this application.
     *
     * @return the instance.
     */
    public static MusicMap getInstance() {
        return instance;
    }

    /**
     * Gets the resources of this application.
     *
     * @return the resources.
     */
    public static Resources getAppResources() {
        return resources;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        resources = getResources();
        registerNetworkCallback();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterNetworkCallback();
    }

    /**
     * Registers the network callback for {@link MusicMap#networkChangeListener}.
     */
    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkChangeListener = new NetworkChangeListener(this);
        connectivityManager.registerDefaultNetworkCallback(networkChangeListener);
    }

    /**
     * Unregisters the network callback for {@link MusicMap#networkChangeListener}.
     */
    private void unregisterNetworkCallback() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkChangeListener);
    }

}
