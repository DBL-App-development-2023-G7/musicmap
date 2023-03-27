package com.example.musicmap.feed;

import static org.junit.Assert.assertEquals;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class MusicMemoryTest {

    private MusicMemory musicMemory;

    @Before
    public void setUp() {
        this.musicMemory = new MusicMemory("author-uid", new Date(),
                new GeoPoint(10, 10), "photo", "song");
    }

    @Test
    public void testGetPhoto() {
        assertEquals(this.musicMemory.getPhoto(), Uri.parse("photo"));
    }

    @Test
    public void testGetSong() {
        assertEquals(this.musicMemory.getSong(), "song");
    }

}
