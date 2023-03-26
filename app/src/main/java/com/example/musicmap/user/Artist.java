package com.example.musicmap.user;

import androidx.annotation.NonNull;

/**
 * The artist class.
 */
public class Artist extends User {

    public Artist(@NonNull ArtistData artistData, @NonNull String uid) {
        super(artistData, uid);
    }

    /**
     * This method retrieves the data associated with this artist.
     *
     * @return the {@code ArtistData} of this artist
     */
    @NonNull
    public ArtistData getArtistData() {
        return (ArtistData) this.getData();
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
