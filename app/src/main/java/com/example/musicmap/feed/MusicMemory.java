package com.example.musicmap.feed;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * The music memory class.
 */
public class MusicMemory extends Post {

    private String photo;
    private String song;

    @SuppressWarnings("unused")
    private MusicMemory() { }

    public MusicMemory(String authorUid, Date timePosted, GeoPoint location, String photo, String song) {
        super(authorUid, timePosted, location);
        this.photo = photo;
        this.song = song;
    }

    public Uri getPhoto() {
        return Uri.parse(photo);
    }

    public String getSong() {
        return song;
    }

    @NonNull
    @Override
    public String toString() {
        return "MusicMemory{" +
                "photo='" + photo + '\'' +
                ", song='" + song + '\'' +
                '}';
    }

}
