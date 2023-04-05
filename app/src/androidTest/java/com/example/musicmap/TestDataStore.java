package com.example.musicmap;

import com.example.musicmap.feed.ConcertMemory;
import com.example.musicmap.feed.MusicMemory;
import com.example.musicmap.feed.Song;
import com.example.musicmap.user.UserData;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class TestDataStore {

    public static final String AUTHOR_UID_THAT_EXISTS_IN_FIREBASE = "9kPlvIKTVUQ3Tp2h9Mh0BpVKdwr1";

    public static UserData getValidUserData() {
        return new UserData("username", "User", "Name", "user@example.com", new Date());
    }

    public static MusicMemory getValidMusicMemory() {
        return new MusicMemory("author-uid", new Date(),
                new GeoPoint(10, 10), "https://imgur.com/photo", new Song(
                "song", "1234", "https://imgur.com/photo-3", "https://spotify.com/preview"
        ));
    }

    public static MusicMemory getValidMusicMemory(String authorId, String songName) {
        return new MusicMemory(authorId, new Date(),
                new GeoPoint(10, 10), "https://imgur.com/photo", new Song(
                songName, "1234", "https://imgur.com/photo-3", "https://spotify.com/preview"
        ));
    }

    public static ConcertMemory getValidConcertMemory() {
        return new ConcertMemory("author-uid", new Date(),
                new GeoPoint(10, 10), "name", "https://youtube.com/video");
    }
}
