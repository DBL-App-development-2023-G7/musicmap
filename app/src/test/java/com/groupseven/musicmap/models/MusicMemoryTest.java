package com.groupseven.musicmap.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;


import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class MusicMemoryTest {

    private MusicMemory musicMemory;
    private Song song;

    @Before
    public void setUp() throws Exception {
        song = new Song("name", "artistName", "spotifyArtistId", "imageUri", "musicPreviewUri");
        GeoPoint geoPoint = new GeoPoint(0.0, 0.0);
        Date date = new Date();
        musicMemory = new MusicMemory("authorUid", date, geoPoint, "photoUri", song);
    }

    @Test
    public void testGetPhoto() {
        // null because of Uri testing defaults
        assertNull(musicMemory.getPhoto());
    }

    @Test
    public void testGetSong() {
        assertEquals(song, musicMemory.getSong());
    }

    @Test
    public void testToString() {
        String expectedString = "MusicMemory{photo='photoUri', song='Song{name='name', artistName=artistName, spotifyArtistId='spotifyArtistId', imageUri='imageUri', musicPreviewUri='musicPreviewUri'}'}";
        String unexpectedString = "MusicMemory{photo='NOTPHOTOURL', song='Song{name='name', artistName=artistName, spotifyArtistId='spotifyArtistId', imageUri='imageUri', musicPreviewUri='musicPreviewUri'}'}";

        assertEquals(expectedString, musicMemory.toString());
        assertNotEquals(unexpectedString, musicMemory.toString());
    }

}