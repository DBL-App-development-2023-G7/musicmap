package com.groupseven.musicmap.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class ArtistTest {

    private ArtistData artistData;
    private Artist artist;
    private Artist unverifiedArtist;

    @Before
    public void setUp() {
        UserData userData = new UserData("username", "User",
                "Name", "user@email.com", new Date(), true);
        artistData = new ArtistData(userData, true, "spotify-id");
        ArtistData unverifiedArtistData = new ArtistData(userData, true, "spotify-id");
        artist = new Artist(artistData, "uid");
        unverifiedArtist = new Artist(unverifiedArtistData, "uid");

    }

    @Test
    public void testGetArtistData() {
        assertEquals(artistData, artist.getArtistData());
    }

    @Test
    public void testIsVerified() {
        assertTrue(artist.isVerified());
        assertFalse(unverifiedArtist.isVerified());
    }

}
