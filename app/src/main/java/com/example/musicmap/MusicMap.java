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
        createInternetCheckNotificationChannel();
        Intent intent = new Intent(this, InternetCheckService.class);
        startService(intent);
    }

    /**
     * Creates a notification channel for the internet check service.
     * The channel's name and importance are set to high.
     */
    private void createInternetCheckNotificationChannel() {
        CharSequence name = "Internet Check";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(Constants.INTERNET_CHECK_NOTIFICATION_CHANNEL,
                name, importance);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}
