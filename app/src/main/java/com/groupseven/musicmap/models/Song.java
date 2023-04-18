package com.groupseven.musicmap.models;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class Song implements Serializable {

    private String name;

    private String artistName;

    private String spotifyArtistId;

    private String imageUri;
    @Nullable
    private String musicPreviewUri;

    @SuppressWarnings("unused")
    public Song() { }

    public Song(String name,
                String artistName,
                String spotifyArtistId,
                String imageUri,
                @Nullable String musicPreviewUri) {
        this.name = name;
        this.artistName = artistName;
        this.spotifyArtistId = spotifyArtistId;
        this.imageUri = imageUri;
        this.musicPreviewUri = musicPreviewUri;
    }

    public Song(Track spotifyTrack) {
        this.name = spotifyTrack.getName();
        this.artistName = spotifyTrack.getArtists()[0].getName();
        this.spotifyArtistId = spotifyTrack.getArtists()[0].getId();
        this.imageUri = spotifyTrack.getAlbum().getImages()[0].getUrl();
        this.musicPreviewUri = spotifyTrack.getPreviewUrl();
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

    public String getArtistName() {
        return this.artistName;
    }

    @Nullable
    public Uri getMusicPreviewUri() {
        if (musicPreviewUri == null) {
            return null;
        }

        return Uri.parse(musicPreviewUri);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(
                "Song{name='%s', artistName=%s, spotifyArtistId='%s', imageUri='%s', musicPreviewUri='%s'}",
                name, artistName, spotifyArtistId, imageUri, musicPreviewUri);
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

}
