package com.example.musicmap.feed;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * The concert memory class.
 */
public class ConcertMemory extends Post {

    private String name;
    private String video;

    @SuppressWarnings("unused")
    public ConcertMemory() { }

    public ConcertMemory(String authorUid, Date timePosted, GeoPoint location, String name, String video) {
        super(authorUid, timePosted, location);
        this.name = name;
        this.video = video;
    }

    public String getName() {
        return name;
    }

    public Uri getVideo() {
        return Uri.parse(video);
    }

    @NonNull
    @Override
    public String toString() {
        return "ConcertMemory{" +
                "name='" + name + '\'' +
                ", video='" + video + '\'' +
                '}';
    }

}
