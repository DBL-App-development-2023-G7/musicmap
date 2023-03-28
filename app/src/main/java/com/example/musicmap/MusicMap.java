package com.example.musicmap;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;

import com.example.musicmap.services.InternetCheckService;
import com.example.musicmap.util.Constants;

/**
 * The main Application.
 */
public class MusicMap extends Application {

    private static MusicMap instance;
    private static Resources resources;

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

        // Start internet check
        Intent intent = new Intent(this, InternetCheckService.class);
        startService(intent);
    }

}
