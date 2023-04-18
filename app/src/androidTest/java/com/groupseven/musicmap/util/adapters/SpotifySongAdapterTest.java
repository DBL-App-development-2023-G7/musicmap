package com.groupseven.musicmap.util.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.TestDataStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import se.michaelthelin.spotify.model_objects.specification.Track;

@RunWith(MockitoJUnitRunner.class)
public class SpotifySongAdapterTest {

    @Mock
    private Activity mockActivity;

    @Mock
    private LayoutInflater mockLayoutInflater;

    private SpotifySongAdapter spotifySongAdapter;

    @Before
    public void setUp() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(TestDataStore.getValidSpotifyTrack());

        when(mockActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(mockLayoutInflater);
        spotifySongAdapter = new SpotifySongAdapter(mockActivity, R.layout.spotify_search_song_layout, tracks);
    }

    @Test
    public void testGetCount() {
        assertEquals(1, spotifySongAdapter.getCount());
    }

    @Test
    public void testGetItem() {
        assertEquals("test-track-name", spotifySongAdapter.getItem(0).getName());
    }

}
