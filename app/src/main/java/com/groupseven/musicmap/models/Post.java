package com.groupseven.musicmap.models;

import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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

    @Exclude
    private final transient TaskCompletionSource<User> authorTask = new TaskCompletionSource<>();

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
     *
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

        // Start fetching User object (executor: run immediately)
        AuthSystem.getUser(authorUid).addOnCompleteListener(Runnable::run, task -> {
            if (task.isSuccessful()) {
                authorTask.setResult(task.getResult());
            } else {
                Exception exception = task.getException();
                assert exception != null;
                authorTask.setException(exception);
            }
        });
    }

    /**
     * Gets the {@link User} that is the author of this post.
     *
     * This does not make a request each time the method is called, feel free to call as often as you like.
     *
     * @return the task generating the {@link User} instance.
     */
    @Exclude
    public Task<User> getAuthorTask() {
        return authorTask.getTask();
    }

}