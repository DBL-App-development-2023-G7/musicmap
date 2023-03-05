package com.example.musicmap.user;

/**
 * The artist class.
 */
public class Artist extends User {

    public Artist(ArtistData artistData, String uid) {
        super(artistData, uid);
    }

    /**
     * This method retrieves the data associated with this artist.
     *
     * @return the {@code ArtistData} of this artist
     */
    public ArtistData getArtistData() {
        return (ArtistData) super.getData();
    }

    /**
     * This method checks if the artist is verified.
     *
     * @return true if the artist is verified
     */
    public boolean isVerified() {
        return getArtistData().getVerified();
    }

}
