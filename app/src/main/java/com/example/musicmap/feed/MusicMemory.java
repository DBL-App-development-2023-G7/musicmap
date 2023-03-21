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
    private final String photo;

    public MusicMemory(String author, Date timePosted, GeoPoint location, String photo) {
        super(author, timePosted, location);
        this.photo = photo;
    }

    public Uri getPhotoUri() {
        return Uri.parse(photo);
    }

    @NonNull
    @Override
    public String toString() {
        return "MusicMemory{"
                + "photo=" + photo
                + '}';
    }

}
