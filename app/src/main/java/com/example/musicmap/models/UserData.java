package com.example.musicmap.models;

import android.net.Uri;

import com.example.musicmap.util.Constants;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * The user data class.
 */
public class UserData {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Date birthdate;
    private String profilePicture;
    private boolean artist;

    UserData() {
        // Used for Firebase POJO deserialization (toObject)
    }

    public UserData(String username, String firstName, String lastName, String email, Date birthdate) {
        this(username, firstName, lastName, email, birthdate, false);
    }

    protected UserData(String username, String firstName, String lastName, String email,
                       Date birthdate, boolean artist) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.artist = artist;
        this.profilePicture = Constants.DEFAULT_USER_IMAGE_URI;
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

    /**
     * This method retrieves the profile picture of the user.
     *
     * @return the profile picture of the user
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * This method retrieves the profile picture uri of the user.
     *
     * @return the profile picture uri of the user
     */
    @Exclude
    public Uri getProfilePictureUri() {
        if (profilePicture.isEmpty()) {
            return Uri.parse(Constants.PROFILE_USER_UID_ARGUMENT);
        }
        return Uri.parse(profilePicture);
    }

    public boolean isArtist() {
        return artist;
    }

    public User toUser(String uid) {
        return new User(this, uid);
    }

}
