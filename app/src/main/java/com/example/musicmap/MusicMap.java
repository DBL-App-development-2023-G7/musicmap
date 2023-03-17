package com.example.musicmap;

import android.app.Application;

/**
 * The main Application.
 */
public class MusicMap extends Application {

    private static MusicMap instance;

    /**
     * Gets the instance of this application.
     *
     * @return the instance.
     */
    public static MusicMap getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
