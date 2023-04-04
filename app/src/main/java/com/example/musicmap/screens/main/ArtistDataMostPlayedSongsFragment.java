package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.musicmap.R;
import com.example.musicmap.screens.artist.PopularSongsAdapter;

import java.util.ArrayList;

public class ArtistDataMostPlayedSongsFragment extends MainFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mostPlayedSongsView = inflater.inflate(R.layout.fragment_most_played_songs, container, false);
        Activity activity = requireActivity();

        PopularSongsAdapter popularSongsAdapter = new PopularSongsAdapter(activity, R.layout.song_layout_artist_data,
                new ArrayList<>());
        ListView feedListView = mostPlayedSongsView.findViewById(R.id.most_played_list);
        feedListView.setAdapter(popularSongsAdapter);

        return mostPlayedSongsView;
    }

}
