package com.example.musicmap.feed;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class ConcertMemory extends FeedObject {

    private String video;

    public ConcertMemory(Date timePosted, GeoPoint location) {
        super(timePosted, location);
    }

    public String getVideo() {
        return video;
    }

}
