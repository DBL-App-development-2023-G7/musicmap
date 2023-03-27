package com.example.musicmap.feed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
                new GeoPoint(10, 10), "photo-1", "song-1"));
        feedItems.add(new MusicMemory("author-uid-2", new Date(),
                new GeoPoint(20, 20), "photo-2", "song-2"));

        when(mockActivity.getLayoutInflater()).thenReturn(mockLayoutInflater);
        when(mockLayoutInflater.inflate(anyInt(), any(ViewGroup.class), anyBoolean())).thenReturn(mock(View.class));
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

    @Test
    public void testGetView() {
        View convertView = mock(View.class);
        ViewGroup parent = mock(ViewGroup.class);

        when(convertView.findViewById(R.id.song_art)).thenReturn(new TextView(mockActivity));
        when(convertView.findViewById(R.id.song_name)).thenReturn(new TextView(mockActivity));
        when(convertView.findViewById(R.id.song_details)).thenReturn(new TextView(mockActivity));
        when(convertView.findViewById(R.id.memory_image)).thenReturn(new ImageView(mockActivity));
        when(convertView.findViewById(R.id.user_profile_image)).thenReturn(new ImageView(mockActivity));

        View view = feedAdapter.getView(0, convertView, parent);

        assertNotNull(view.findViewById(R.id.song_art));
        assertNotNull(view.findViewById(R.id.song_name));
        assertNotNull(view.findViewById(R.id.song_details));
        assertNotNull(view.findViewById(R.id.memory_image));
        assertNotNull(view.findViewById(R.id.user_profile_image));
    }
}

