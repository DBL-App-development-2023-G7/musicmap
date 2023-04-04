package com.example.musicmap.feed;

import static org.junit.Assert.assertEquals;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.musicmap.TestDataStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MusicMemoryTest {

    private MusicMemory musicMemory;

    @Before
    public void setUp() {
        this.musicMemory = TestDataStore.getValidMusicMemory();
    }

    @Test
    public void testGetPhoto() {
        assertEquals(this.musicMemory.getPhoto(), Uri.parse("https://imgur.com/photo"));
    }

    @Test
    public void testGetSong() {
        assertEquals(this.musicMemory.getSong().getName(), "song");
    }

}
