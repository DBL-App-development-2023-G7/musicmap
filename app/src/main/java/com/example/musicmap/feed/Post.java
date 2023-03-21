package com.example.musicmap.feed;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * A post that can be displayed on the feed or map.
 */
public abstract class Post {

    private final String author;
    private final Date timePosted;
    private final GeoPoint location;

    public Post(String author, Date timePosted, GeoPoint location) {
        this.author = author;
        this.timePosted = timePosted;
        this.location = location;
    }

    public String getAuthor() {
        return author;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public GeoPoint getLocation() {
        return location;
    }

}
