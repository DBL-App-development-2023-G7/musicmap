package com.example.musicmap.feed;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.example.musicmap.R;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FeedAdapterTest {

    @Mock
    private Activity mockActivity;

    @Mock
    private LayoutInflater mockLayoutInflater;

    private FeedAdapter feedAdapter;

    @Before
    public void setUp() {
        List<MusicMemory> feedItems = new ArrayList<>();
        feedItems.add(new MusicMemory("author-uid-1", new Date(),
                new GeoPoint(10, 10), "https://imgur.com/photo-1", "song-1"));
        feedItems.add(new MusicMemory("author-uid-2", new Date(),
                new GeoPoint(20, 20), "https://imgur.com/photo-2", "song-2"));

        when(mockActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(mockLayoutInflater);
        feedAdapter = new FeedAdapter(mockActivity, R.layout.single_post_layout_feed, feedItems);
    }

    @Test
    public void testGetCount() {
        assertEquals(2, feedAdapter.getCount());
    }

    @Test
    public void testGetItem() {
        assertEquals("song-1", feedAdapter.getItem(0).getSong());
        assertEquals("song-2", feedAdapter.getItem(1).getSong());
    }
}

