package com.example.musicmap.screens.main;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.musicmap.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class SpotifyWidgetFragment extends Fragment {

    private TextView artistName;
    private TextView songName;
    private ImageView albumArt;
    private MediaPlayer mediaPlayer;

    private ImageView playImageView;
    private Uri previewUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View widgetView = inflater.inflate(R.layout.spotify_playback_widget_layout, container, false);

        this.artistName = widgetView.findViewById(R.id.artist_name);
        this.songName = widgetView.findViewById(R.id.song_name);
        this.albumArt = widgetView.findViewById(R.id.spotify_album_cover);

        playImageView = widgetView.findViewById(R.id.play_imageView);

        return widgetView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mediaPlayer.isPlaying()) {
            pauseAudio();
        }
    }

    public void setupFragment(String songName, String artistName, String photoURI, Uri previewUri) {
        this.songName.setText(songName);
        this.artistName.setText(artistName);
        this.previewUri = previewUri;

        Picasso.get().load(photoURI).into(albumArt);

        setupMediaPlayer();

        playImageView.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                pauseAudio();
            } else {
                playAudio();
            }

        });
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(unused -> pauseAudio());

        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());

        try {
            mediaPlayer.setDataSource(this.getContext(), previewUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSongName(String songName) {
        this.songName.setText(songName);
    }

    public void setArtistName(String artistName) {
        this.artistName.setText(artistName);
    }

    private void playAudio() {
        mediaPlayer.start();
        playImageView.setImageResource(R.drawable.pause_icon);
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        playImageView.setImageResource(R.drawable.play_icon);
    }
}