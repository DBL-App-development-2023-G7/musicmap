package com.groupseven.musicmap;

import com.google.firebase.firestore.GeoPoint;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.models.Song;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.models.UserData;

import java.util.Date;

public class TestDataStore {

    public static final String AUTHOR_UID_THAT_EXISTS_IN_FIREBASE = "42nWn01PbdQTuVrqEmrYrl6CXMp1";

    public static UserData getValidUserData() {
        return new UserData("username", "User", "Name", "user@example.com", new Date());
    }

    public static User getValidUser() {
        return new User(getValidUserData(), "uid");
    }

    public static MusicMemory getValidMusicMemory() {
        return getValidMusicMemory("author-uid", "song");
    }

    public static MusicMemory getValidMusicMemory(String authorId, String songName) {
        return new MusicMemory(authorId, new Date(),
                new GeoPoint(10, 10), "https://imgur.com/photo1", new Song(
                songName, "MrMusic", "1234", "https://imgur.com/photo2", "https://spotify.com/preview"
        ));
    }

}
