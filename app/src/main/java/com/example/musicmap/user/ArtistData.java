package com.example.musicmap.user;

/**
 * The artist data class.
 */
public class ArtistData extends UserData {

    private boolean verified;


    public ArtistData() {
        // Required for setting the data using the toObject() method
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
}
