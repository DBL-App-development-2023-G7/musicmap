package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.musicmap.R;
import com.example.musicmap.feed.Song;
import com.example.musicmap.screens.artist.PopularSongsAdapter;
import com.example.musicmap.user.Artist;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.Queries;

import java.util.ArrayList;
import java.util.List;

public class ArtistDataMostPlayedSongsFragment extends MainFragment {

    private static final String TAG = "ArtistDataMostPlayedSongsFragment";
    private static final int NUMBER_OF_SONGS = 10;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mostPlayedSongsView = inflater.inflate(R.layout.fragment_most_played_songs, container, false);
        Activity activity = requireActivity();

        PopularSongsAdapter popularSongsAdapter = new PopularSongsAdapter(activity, R.layout.song_layout_artist_data,
                new ArrayList<>());
        ListView feedListView = mostPlayedSongsView.findViewById(R.id.most_played_list);
        feedListView.setAdapter(popularSongsAdapter);
        User user = Session.getInstance().getCurrentUser();

        if (!user.isArtist()) {
            throw new IllegalStateException("ArtistDataMostPopularSongsFragment cannot be served for non-artist user.");
        }

        Artist artist = (Artist) user;
        String spotifyArtistId = artist.getArtistData().getUsername();

        Queries.getMostPopularSongsByArtist(spotifyArtistId, NUMBER_OF_SONGS).addOnCompleteListener(completedTask -> {
            if (completedTask.isSuccessful()) {
                List<Song> topSongs = completedTask.getResult();
                popularSongsAdapter.addAll(topSongs);
                popularSongsAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Exception occurred while getting most popular songs", completedTask.getException());
            }
        });

        return mostPlayedSongsView;
    }

}
