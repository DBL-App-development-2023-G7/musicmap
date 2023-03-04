package com.example.musicmap.feed;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public abstract class FeedObject {
    private final Date timePosted;
    private final GeoPoint location;

    public FeedObject(Date timePosted, GeoPoint location) {
        this.timePosted = timePosted;
        this.location = location;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public GeoPoint getLocation() {
        return location;
    }

}
