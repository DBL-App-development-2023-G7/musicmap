package com.example.musicmap.screens.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.musicmap.R;
import com.squareup.picasso.Picasso;

public class SpotifyWidgetFragment extends Fragment {

    private boolean playing;
    private TextView artistName;
    private TextView songName;
    private ImageView albumArt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View widgetView = inflater.inflate(R.layout.spotify_playback_widget_layout, container, false);

        this.artistName = widgetView.findViewById(R.id.artist_name);
        this.songName = widgetView.findViewById(R.id.song_name);
        this.albumArt = widgetView.findViewById(R.id.spotify_album_cover);

        ImageView playImageView = widgetView.findViewById(R.id.play_imageView);
        playImageView.setOnClickListener(view -> {
            if (playing) {
                playImageView.setImageResource(R.drawable.play_icon);
            } else {
                playImageView.setImageResource(R.drawable.pause_icon);
            }

            playing = !playing;
        });

        return widgetView;
    }

    public void setupFragment(String songName, String artistName, String photoURI){
        this.songName.setText(songName);
        this.artistName.setText(artistName);

        Picasso.get().load(photoURI).into(albumArt);
    }

    public void setSongName(String songName){
        this.songName.setText(songName);
    }

    public void setArtistName(String artistName){
        this.artistName.setText(artistName);
    }

}