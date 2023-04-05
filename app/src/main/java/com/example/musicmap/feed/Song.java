package com.example.musicmap.feed;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Objects;

import java.util.Objects;

public class Song {

    private String name;

    private String spotifyArtistId;

    private String imageUri;
    private String musicPreviewUri;

    @SuppressWarnings("unused")
    public Song() { }

    public Song(String name,
                String spotifyArtistId,
                String imageUri,
                String musicPreviewUri) {
        this.name = name;
        this.spotifyArtistId = spotifyArtistId;
        this.imageUri = imageUri;
        this.musicPreviewUri = musicPreviewUri;
    }

    public String getName() {
        return name;
    }

    public String getSpotifyArtistId() {
        return spotifyArtistId;
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
                "Song{name='%s', spotifyArtistId='%s', imageUri='%s', musicPreviewUri='%s'}",
                name, spotifyArtistId, imageUri, musicPreviewUri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Song song = (Song) o;
        return Objects.equals(name, song.name) && Objects.equals(spotifyArtistId, song.spotifyArtistId)
                && Objects.equals(imageUri, song.imageUri) && Objects.equals(musicPreviewUri, song.musicPreviewUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, spotifyArtistId, imageUri, musicPreviewUri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Song song = (Song) o;
        return Objects.equals(name, song.name) && Objects.equals(spotifyArtistId, song.spotifyArtistId)
                && Objects.equals(artistUid, song.artistUid) && Objects.equals(imageUri, song.imageUri)
                && Objects.equals(musicPreviewUri, song.musicPreviewUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, spotifyArtistId, artistUid, imageUri, musicPreviewUri);
    }

}
