package com.example.musicmap.user;

import java.util.Date;

/**
 * The user.
 */
public class UserData {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Date birthdate;

    private boolean artist;

    public UserData() {

    }

    public UserData(String username, String firstName, String lastName, String email,
                    Date birthdate, boolean artist) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.artist = artist;
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
     * This method retrieves the birthdate of the user as a {@code Date}.
     *
     * @return the birthdate of the user
     */
    public Date getBirthdate() {
        return birthdate;
    }

    public boolean isArtist() {
        return artist;
    }

}
