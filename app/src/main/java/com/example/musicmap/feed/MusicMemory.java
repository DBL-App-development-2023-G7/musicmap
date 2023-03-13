package com.example.musicmap.feed;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * The music memory class.
 */
public class MusicMemory extends Post {

    // TODO remaining details, e.g. song reference
    private final Uri photo;

    public MusicMemory(Date timePosted, GeoPoint location, Uri photo) {
        super(timePosted, location);
        this.photo = photo;
    }

    public Uri getPhoto() {
        return photo;
    }

    @NonNull
    @Override
    public String toString() {
        return "MusicMemory{"
                + "photo=" + photo
                + '}';
    }

}
