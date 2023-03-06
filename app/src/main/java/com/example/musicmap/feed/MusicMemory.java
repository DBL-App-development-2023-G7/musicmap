package com.example.musicmap.feed;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * The music memory class.
 */
public class MusicMemory extends FeedObject {

    private String photo;

    public MusicMemory(Date timePosted, GeoPoint location) {
        super(timePosted, location);
    }

    public String getPhoto() {
        return photo;
    }
    
}
