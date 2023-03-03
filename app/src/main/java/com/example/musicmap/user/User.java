package com.example.musicmap.user;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The user.
 */
public class User {

    protected UserData data;
    private String uid;

    public User(UserData data, String uid) {
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
     *
     * @return
     */
    public UserData getData() {
        return data;
    }
}
