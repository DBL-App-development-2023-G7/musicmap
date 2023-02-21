package com.example.musicmap.user;

import java.util.UUID;

/**
 * The user.
 */
public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UUID uuid;

    public User(String username, String firstName, String lastName, String email, UUID uuid) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uuid = uuid;
    }

    /**
     * This method retrieves the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method retrieves the first name (given name) of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This method retrieves the last name (family name) of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This method retrieves the email of the user.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method retrives the uuid of the user.
     *
     * @return the uuid of the user
     */
    public UUID getUuid() {
        return uuid;
    }
}
