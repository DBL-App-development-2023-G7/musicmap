package com.groupseven.musicmap.models;

import androidx.annotation.NonNull;

/**
 * The user class.
 */
public class User {

    @NonNull
    private final UserData data;
    private final String uid;

    public User(@NonNull UserData data, @NonNull String uid) {
        this.data = data;
        this.uid = uid;
    }

    /**
     * This method retrieves the uid of the user.
     *
     * @return the uid of the user
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method retrieves the user's data.
     *
     * @return the {@code UserData} of this user
     */
    @NonNull
    public UserData getData() {
        return data;
    }

    /**
     * This method returns true if the user is an artist.
     *
     * @return true if the user is an artist
     */
    public boolean isArtist() {
        return data.isArtist();
    }

}
