package com.example.musicmap.feed;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.example.musicmap.R;
import com.example.musicmap.TestDataStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
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
        feedItems.add(TestDataStore.getValidMusicMemory());
        feedItems.add(TestDataStore.getValidMusicMemory("author-uid-2", "song-2"));

        when(mockActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(mockLayoutInflater);
        feedAdapter = new FeedAdapter(mockActivity, R.layout.single_post_layout_feed, feedItems);
    }

    @Test
    public void testGetCount() {
        assertEquals(2, feedAdapter.getCount());
    }

    @Test
    public void testGetItem() {
        assertEquals("song", feedAdapter.getItem(0).getSong().getName());
        assertEquals("song-2", feedAdapter.getItem(1).getSong().getName());
    }

}

