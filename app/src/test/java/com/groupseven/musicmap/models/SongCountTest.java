package com.groupseven.musicmap.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;


public class SongCountTest {

    Song song;
    SongCount songCount;

    @Before
    public void setUp() {
        song = new Song("Test song", "Test artist", "12345", "http://test.com/image.jpg", "http://test.com/preview.mp3");
        songCount = new SongCount(song, 10L);

    }

    @Test
    public void testGetSong() {
        assertEquals(song, songCount.getSong());
    }

    @Test
    public void testGetCount() {
        assertEquals(10L, songCount.getCount());
    }

}

