package com.groupseven.musicmap.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

public class SongTest {

    private Song song;
    private Song songWithoutPreview;

    @Before
    public void setUp() {
        song = new Song("Test song",
                "Test artist",
                "12345",
                "http://test.com/image.jpg",
                "http://test.com/preview.mp3"
        );
        songWithoutPreview = new Song("Test song",
                "Test artist",
                "12345",
                "http://test.com/image.jpg",
                null
        );

    }

    @Test
    public void testGetName() {
        assertEquals("Test song", song.getName());
    }

    @Test
    public void testGetArtistName() {
        assertEquals("Test artist", song.getArtistName());
    }

    @Test
    public void testGetSpotifyArtistId() {
        assertEquals("12345", song.getSpotifyArtistId());
    }

    @Test
    public void testGetImageUri() {
        // null since the uri object is mocked
        // otherwise it throws a runtime exception with message:
        // "Method parse in android.net.Uri not mocked"
        assertEquals(null, song.getImageUri());
    }

    @Test
    public void testGetMusicPreviewUri() {
        assertEquals(null, song.getMusicPreviewUri());
        assertNull(songWithoutPreview.getMusicPreviewUri());
    }

    @Test
    public void testEquals() {

        Song song1 = new Song(
                "Test song",
                "Test artist",
                "12345",
                "http://test.com/image.jpg",
                "http://test.com/preview.mp3"
        );

        Song song2 = new Song("Test song",
                "Test artist",
                "12345",
                "http://test.com/image.jpg",
                "http://test.com/preview.mp3"
        );

        Song song3 = new Song("Another test song",
                "Test artist",
                "12345",
                "http://test.com/image.jpg",
                "http://test.com/preview.mp3"
        );

        assertEquals(song1, song2);
        assertNotEquals(song1, song3);
    }

}