package com.example.musicmap.feed;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * The concert memory class.
 */
public class ConcertMemory extends Post {

    // TODO remaining details, e.g. name of concert
    private final String video;

    public ConcertMemory(Date timePosted, GeoPoint location, String video) {
        super(timePosted, location);
        this.video = video;
    }

    public String getVideo() {
        return video;
    }

}
