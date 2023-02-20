package com.example.musicmap.user;

import java.util.UUID;

/**
 *  The user.
 */
public class User {
    private String username;
    private String email;
    private UUID uuid;

    /**
     * This method retrieves the username of the user.
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method retrieves the email of the user.
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method retrives the uuid of the user.
     * @return the uuid of the user
     */
    public UUID getUuid() {
        return uuid;
    }
}
