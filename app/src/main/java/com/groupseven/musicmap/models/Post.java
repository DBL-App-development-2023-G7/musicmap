package com.groupseven.musicmap.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * A post that can be displayed on the feed or map.
 */
public abstract class Post {

    @DocumentId
    private String uid;

    private String authorUid;
    private Date timePosted;
    private GeoPoint location;

    protected Post() { }

    public Post(String authorUid, Date timePosted, GeoPoint location) {
        setAuthorUid(authorUid);
        this.timePosted = timePosted;
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    @Exclude
    public String getAuthorUid() {
        return authorUid;
    }

    public Date getTimePosted() {
        return timePosted;
    }

    public GeoPoint getLocation() {
        return location;
    }

    /**
     * Sets the author UID of this post.
     * <p>
     * Don't use when the author is already initialized!
     *
     * @param authorUid the author UID.
     * @throws IllegalStateException if the authorUid is already set.
     */
    public void setAuthorUid(String authorUid) throws IllegalStateException {
        if (this.authorUid != null) {
            throw new IllegalStateException("authorUid is already set");
        }

        this.authorUid = authorUid;
    }

}
