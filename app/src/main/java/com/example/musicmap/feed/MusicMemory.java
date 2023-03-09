package com.example.musicmap.feed;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * The music memory class.
 */
public class MusicMemory extends FeedObject {

    // TODO remaining details, e.g. song reference
    private final String photo;

    public MusicMemory(Date timePosted, GeoPoint location, String photo) {
        super(timePosted, location);
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }
    
}