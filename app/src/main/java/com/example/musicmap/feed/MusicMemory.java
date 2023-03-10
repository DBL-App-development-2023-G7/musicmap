package com.example.musicmap.feed;

import android.net.Uri;
import com.google.firebase.firestore.GeoPoint;
import java.util.Date;

/**
 * The music memory class.
 */
public class MusicMemory extends LocationAndTime {
    @Override
    public String toString() {
        return "MusicMemory{"
                + "photo=" + photo
                + '}';
    }

    // TODO remaining details, e.g. song reference
    private final Uri photo;

    public MusicMemory(Date timePosted, GeoPoint location, Uri photo) {
        super(timePosted, location);
        this.photo = photo;
    }

    public Uri getPhoto() {
        return photo;
    }

}
