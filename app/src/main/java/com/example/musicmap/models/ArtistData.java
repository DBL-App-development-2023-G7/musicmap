package com.example.musicmap.models;

/**
 * The artist data class.
 */
public class ArtistData extends UserData {

    private boolean verified;

    private String spotifyId;

    @SuppressWarnings("unused")
    ArtistData() {
        // Used for Firebase POJO deserialization (toObject)
    }

    public ArtistData(UserData userData, boolean verified) {
        this(userData, verified, null);
    }

    public ArtistData(UserData userData, boolean verified, String spotifyId) {
        super(userData.getUsername(), userData.getFirstName(), userData.getLastName(),
                userData.getEmail(), userData.getBirthdate(), true);
        this.verified = verified;
        this.spotifyId = spotifyId;
    }

    /**
     * This method retrieves the verification status of the artist.
     *
     * @return the verification status of the artist
     */
    public boolean isVerified() {
        return verified && spotifyId != null;
    }

    /**
     * Gets the Spotify ID of the artist.
     *
     * @return the Spotify ID.
     */
    public String getSpotifyId() {
        return spotifyId;
    }

    @Override
    public Artist toUser(String uid) {
        return new Artist(this, uid);
    }

}
