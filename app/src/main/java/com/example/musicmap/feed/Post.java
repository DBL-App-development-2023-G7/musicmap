package com.example.musicmap.feed;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * A post that can be displayed on the feed or map.
 */
public abstract class Post {

    // TODO add poster (user who made post)
    private final Date timePosted;
    private final GeoPoint location;

    public Post(Date timePosted, GeoPoint location) {
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
