package com.example.musicmap.feed;

import android.net.Uri;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
