package com.example.musicmap.user;

/**
 * The artist data class.
 */
public class ArtistData extends UserData {

    private boolean verified;

    @SuppressWarnings("unused")
    ArtistData() {
        // Used for Firebase POJO deserialization (toObject)
    }

    public ArtistData(UserData userData, boolean verified) {
        super(userData.getUsername(), userData.getFirstName(), userData.getLastName(),
                userData.getEmail(), userData.getBirthdate(), true);
        this.verified = verified;
    }

    /**
     * This method retrieves the verification status of the artist.
     *
     * @return the verification status of the artist
     */
    public boolean getVerified() {
        return verified;
    }

    @Override
    public Artist toUser(String uid) {
        return new Artist(this, uid);
    }

}
