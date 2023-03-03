package com.example.musicmap.user;

import java.util.Map;

/**
 * The user.
 */
public class ArtistData extends UserData {

    private final boolean verified;
    public ArtistData(UserData userData, boolean verified) {
        super(userData.getUsername(), userData.getFirstName(), userData.getLastName(),
                userData.getEmail(), userData.getBirthdate());
        this.verified = verified;
    }

//    public ArtistData(String username, String firstName, String lastName, String email,
//                      Date birthdate) {
//        super(username, firstName, lastName, email, birthdate);
//    }

    /**
     * This method retrieves all user attributes ready to be placed inside a Firestore Database.
     *
     * @return the user attributes as a {@code Map<String, Object>}
     */
    @Override
    public Map<String, Object> getFirestoreAttributes() {
        Map<String, Object> attributes = super.getFirestoreAttributes();
        attributes.put("artist", true);
        attributes.put("verified", verified);
        return attributes;
    }

}
