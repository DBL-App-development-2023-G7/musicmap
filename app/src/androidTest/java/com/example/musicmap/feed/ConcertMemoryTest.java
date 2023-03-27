package com.example.musicmap.feed;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.musicmap.feed.ConcertMemory;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
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
                new GeoPoint(10, 10), "name", "video");
    }

    @Test
    public void testGetName() {
        Assert.assertEquals(this.concertMemory.getName(), "name");
    }

    @Test
    public void testGetSong() {
        Assert.assertEquals(this.concertMemory.getVideo(), Uri.parse("video"));
    }

}

