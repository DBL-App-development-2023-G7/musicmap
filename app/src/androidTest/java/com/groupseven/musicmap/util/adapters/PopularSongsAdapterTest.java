package com.groupseven.musicmap.util.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.TestDataStore;
import com.groupseven.musicmap.models.SongCount;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PopularSongsAdapterTest {

    @Mock
    private Activity mockActivity;

    @Mock
    private LayoutInflater mockLayoutInflater;

    private PopularSongsAdapter popularSongsAdapter;

    @Before
    public void setUp() {
        List<SongCount> songs = new ArrayList<>();
        songs.add(new SongCount(TestDataStore.getValidSong("test-song-1"), 2L));
        songs.add(new SongCount(TestDataStore.getValidSong("test-song-2"), 1L));

        when(mockActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(mockLayoutInflater);
        popularSongsAdapter = new PopularSongsAdapter(mockActivity, R.layout.song_layout_artist_data, songs);
    }

    @Test
    public void testGetCount() {
        assertEquals(2, popularSongsAdapter.getCount());
    }

    @Test
    public void testGetItem() {
        assertEquals("test-song-1", popularSongsAdapter.getItem(0).getSong().getName());
        assertEquals(2L, popularSongsAdapter.getItem(0).getCount());
        assertEquals("test-song-2", popularSongsAdapter.getItem(1).getSong().getName());
        assertEquals(1L, popularSongsAdapter.getItem(1).getCount());
    }

}
