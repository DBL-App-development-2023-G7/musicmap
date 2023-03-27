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
public class ConcertMemoryTest {

    private ConcertMemory concertMemory;

    @Before
    public void setUp() {
        this.concertMemory = new ConcertMemory("author-uid", new Date(),
                new GeoPoint(10, 10), "name", "https://youtube.com/video");
    }

    @Test
    public void testGetName() {
        assertEquals(this.concertMemory.getName(), "name");
    }

    @Test
    public void testGetSong() {
        assertEquals(this.concertMemory.getVideo(), Uri.parse("https://youtube.com/video"));
    }

}

