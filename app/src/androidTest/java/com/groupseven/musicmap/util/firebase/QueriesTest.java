package com.groupseven.musicmap.util.firebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.groupseven.musicmap.TestDataStore;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.models.SongCount;
import com.groupseven.musicmap.models.User;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class QueriesTest {

    @Test
    public void testGetUsersByUsername_success_exists() {
        CompletableFuture<User> future = Queries.getUserWithUsername(TestDataStore.USERNAME_THAT_EXISTS_IN_FIREBASE);
        User user = future.join();
        assertNotNull(user);
        assertEquals(user.getData().getUsername(), TestDataStore.USERNAME_THAT_EXISTS_IN_FIREBASE);
    }

    @Test
    public void testGetUsersByUsername_success_doesNotExist() {
        String userNameThatDoesNotExistsInFirebase = "some-username";
        CompletableFuture<User> future = Queries.getUserWithUsername(userNameThatDoesNotExistsInFirebase);
        User user = future.join();
        assertNull(user);
    }

    @Test
    public void testGetMusicMemoryByAuthorIdAndId_success() {
        CompletableFuture<MusicMemory> future = Queries.getMusicMemoryByAuthorIdAndId(
                TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE,
                TestDataStore.MUSIC_MEMORY_ID_THAT_EXISTS_IN_FIREBASE);
        MusicMemory musicMemory = future.join();
        assertNotNull(musicMemory);
        assertEquals(musicMemory.getAuthorUid(), TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE);
        assertEquals(musicMemory.getUid(), TestDataStore.MUSIC_MEMORY_ID_THAT_EXISTS_IN_FIREBASE);
    }

    @Test
    public void testGetMusicMemoriesByAuthorId_success() {
        CompletableFuture<List<MusicMemory>> future = Queries.getMusicMemoriesByAuthorId(
                TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE);
        List<MusicMemory> musicMemories = future.join();
        assertFalse(musicMemories.isEmpty());
        assertEquals(musicMemories.get(0).getAuthorUid(), TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE);
    }

    @Test
    public void testGetAllMusicMemoriesInLastTwentyFourHours_success() {
        long timestamp24HoursAgo = System.currentTimeMillis() - (TimeUnit.HOURS.toMillis(24)
                + TimeUnit.SECONDS.toMillis(5)); // +5 seconds to account for fetching delays
        CompletableFuture<List<MusicMemory>> future = Queries.getAllMusicMemoriesInLastTwentyFourHours();
        List<MusicMemory> musicMemories = future.join();
        musicMemories.forEach(musicMemory -> {
            assertTrue(musicMemory.getTimePosted().getTime() >= timestamp24HoursAgo);
        });
    }

    @Test
    public void testGetAllMusicMemoriesWithSpotifyArtistId_success() {
        CompletableFuture<List<MusicMemory>> task = Queries.getAllMusicMemoriesWithSpotifyArtistId(
                TestDataStore.SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE);
        List<MusicMemory> musicMemories = task.join();
        musicMemories.forEach(musicMemory -> {
            assertEquals(musicMemory.getSong().getSpotifyArtistId(),
                    TestDataStore.SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE);
        });
    }

    @Test
    public void testGetMostPopularSongsByArtist_success() {
        CompletableFuture<List<SongCount>> future = Queries.getMostPopularSongsByArtist(
                TestDataStore.SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE, 5);
        List<SongCount> songCounts = future.join();
        AtomicLong currentCount = new AtomicLong(Long.MAX_VALUE);
        songCounts.forEach(songCount -> {
            assertEquals(songCount.getSong().getSpotifyArtistId(),
                    TestDataStore.SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE);
            assertTrue(songCount.getCount() < currentCount.get());
            currentCount.set(songCount.getCount());
        });
    }

}
