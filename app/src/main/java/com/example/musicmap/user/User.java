package com.example.musicmap.user;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The user.
 */
public class User {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Date birthdate;
    private final String uuid;

    public User(String username, String firstName, String lastName, String email, Date birthdate,
                String uuid) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
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
     * This method retrieves the birthdate of the user as a {@code Date}.
     *
     * @return the birthdate of the user
     */
    public Date getBirthdate() {
        return birthdate;
    }

    /**
     * This method retrieves the birthdate of the user as a {@code Firebase.Timestamp}.
     *
     * @return the birthdate of the user as a {@code Firebase.Timestamp}
     */
    public Timestamp getBirthdateTimestamp() {
        return new Timestamp(birthdate);
    }

    /**
     * This method retrieves the uuid of the user.
     *
     * @return the uuid of the user
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * This method retrieves all user attributes ready to be placed inside a Firestore Database.
     *
     * @return the user attributes as a {@code Map<String, Object>}
     */
    public Map<String, Object> getFirestoreAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("username", username);
        attributes.put("firstName", firstName);
        attributes.put("lastName", lastName);
        attributes.put("birthdate", this.getBirthdateTimestamp());
        attributes.put("artist", false);
        return attributes;
    }

}
