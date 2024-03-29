package com.groupseven.musicmap;

import com.google.firebase.firestore.GeoPoint;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.models.UserData;

import java.util.Date;

import se.michaelthelin.spotify.model_objects.specification.Track;

public class TestDataStore {

    public static final String AUTHOR_UID_THAT_EXISTS_IN_FIREBASE = "42nWn01PbdQTuVrqEmrYrl6CXMp1";
    public static final String SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE = "1dfeR4HaWDbWqFHLkxsg1d";
    public static final String MUSIC_MEMORY_ID_THAT_EXISTS_IN_FIREBASE = "N4yBvYoo76MzL0WSLqhd";
    public static final String USERNAME_THAT_EXISTS_IN_FIREBASE = "TPGamesNL";

    public static UserData getValidUserData() {
        return new UserData("testcase-username", "Test", "User",
                "test-user@test.com", new Date());
    }

    public static User getValidUser() {
        return new User(getValidUserData(), "uid");
    }

    public static MusicMemory getValidMusicMemory() {
        return getValidMusicMemory("test-author-uid", "song");
    }

    public static Song getValidSong(String songName) {
        return new Song(songName, "TestMusicArtist", "TestArtistId",
                "https://imgur.com/photo2", "https://spotify.com/preview"
        );
    }

    public static MusicMemory getValidMusicMemory(String authorId, String songName) {
        return new MusicMemory(authorId, new Date(),
                new GeoPoint(10, 10), "https://imgur.com/photo1", getValidSong(songName));
    }

    public static Track getValidSpotifyTrack() {
        Track.Builder builder = new Track.Builder();
        builder.setName("test-track-name");
        return builder.build();
    }

}
