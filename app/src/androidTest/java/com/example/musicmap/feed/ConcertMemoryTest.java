package com.example.musicmap.feed;

import static org.junit.Assert.assertEquals;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.musicmap.TestDataStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ConcertMemoryTest {

    private ConcertMemory concertMemory;

    @Before
    public void setUp() {
        this.concertMemory = TestDataStore.getValidConcertMemory();
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

