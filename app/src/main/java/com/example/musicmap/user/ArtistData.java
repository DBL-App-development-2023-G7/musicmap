package com.example.musicmap.user;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The user.
 */
public class ArtistData extends UserData {

    public ArtistData(String username, String firstName, String lastName, String email,
                      Date birthdate) {
        super(username, firstName, lastName, email, birthdate);
    }

    /**
     * This method retrieves all user attributes ready to be placed inside a Firestore Database.
     *
     * @return the user attributes as a {@code Map<String, Object>}
     */
    public Map<String, Object> getFirestoreAttributes() {
        Map<String, Object> attributes = super.getFirestoreAttributes();
        attributes.put("artist", true);
        return attributes;
    }

}
