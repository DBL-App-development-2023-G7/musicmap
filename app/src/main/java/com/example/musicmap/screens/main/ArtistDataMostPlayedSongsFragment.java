package com.example.musicmap.screens.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.musicmap.R;

public class ArtistDataMostPlayedSongsFragment extends MainFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mostPlayedSongsView = inflater.inflate(R.layout.fragment_most_played_songs, container, false);
        Activity activity = requireActivity();


        return mostPlayedSongsView;
    }

}
