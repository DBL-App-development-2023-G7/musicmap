package com.example.musicmap.feed;

import android.net.Uri;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

public class Song {

    private String name;

    private String spotifyArtistId;
    @Nullable
    private String artistUid;

    private String imageUri;
    private String musicPreviewUri;

    @SuppressWarnings("unused")
    public Song() { }

    public Song(String name,
                String spotifyArtistId,
                @Nullable String artistUid,
                String imageUri,
                String musicPreviewUri) {
        this.name = name;
        this.spotifyArtistId = spotifyArtistId;
        this.artistUid = artistUid;
        this.imageUri = imageUri;
        this.musicPreviewUri = musicPreviewUri;
    }

    public String getName() {
        return name;
    }

    public String getSpotifyArtistId() {
        return spotifyArtistId;
    }

    @Nullable
    public String getArtistUid() {
        return artistUid;
    }

    public Uri getImageUri() {
        return Uri.parse(imageUri);
    }

    public Uri getMusicPreviewUri() {
        return Uri.parse(musicPreviewUri);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(
                "Song{name='%s', spotifyArtistId='%s', artistUid='%s', imageUri='%s', musicPreviewUri='%s'}",
                name, spotifyArtistId, artistUid, imageUri, musicPreviewUri);
    }

}
