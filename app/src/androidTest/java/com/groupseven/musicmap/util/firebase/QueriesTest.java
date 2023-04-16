package com.groupseven.musicmap.util.firebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.groupseven.musicmap.TestDataStore;
import com.groupseven.musicmap.models.MusicMemory;
import com.groupseven.musicmap.models.SongCount;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.util.TaskUtil;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class QueriesTest {

    @Test
    public void testGetUsersByUsername_success_exists() throws ExecutionException, InterruptedException {
        Task<User> task = TaskUtil.getTask(Queries.getUserWithUsername(TestDataStore.USERNAME_THAT_EXISTS_IN_FIREBASE));
        User user = Tasks.await(task);
        assertNotNull(user);
        assertEquals(user.getData().getUsername(), TestDataStore.USERNAME_THAT_EXISTS_IN_FIREBASE);
    }

    @Test
    public void testGetUsersByUsername_success_doesNotExist() throws ExecutionException, InterruptedException {
        String userNameThatDoesNotExistsInFirebase = "some-username";
        Task<User> task = TaskUtil.getTask(Queries.getUserWithUsername(userNameThatDoesNotExistsInFirebase));
        User user = Tasks.await(task);
        assertNull(user);
    }

    @Test
    public void testGetMusicMemoryByAuthorIdAndId_success() throws ExecutionException, InterruptedException {
        Task<MusicMemory> task = TaskUtil.getTask(Queries.getMusicMemoryByAuthorIdAndId(
                TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE,
                TestDataStore.MUSIC_MEMORY_ID_THAT_EXISTS_IN_FIREBASE));
        MusicMemory musicMemory = Tasks.await(task);
        assertNotNull(musicMemory);
        assertEquals(musicMemory.getAuthorUid(), TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE);
        assertEquals(musicMemory.getUid(), TestDataStore.MUSIC_MEMORY_ID_THAT_EXISTS_IN_FIREBASE);
    }

    @Test
    public void testGetMusicMemoriesByAuthorId_success() throws ExecutionException, InterruptedException {
        Task<List<MusicMemory>> task = TaskUtil.getTask(Queries.getMusicMemoriesByAuthorId(
                TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE));
        List<MusicMemory> musicMemories = Tasks.await(task);
        assertFalse(musicMemories.isEmpty());
        assertEquals(musicMemories.get(0).getAuthorUid(), TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE);
    }

    @Test
    public void testGetAllMusicMemoriesInLastTwentyFourHours_success() throws ExecutionException, InterruptedException {
        long timestamp24HoursAgo = System.currentTimeMillis() - (TimeUnit.HOURS.toMillis(24)
                + TimeUnit.SECONDS.toMillis(5)); // +5 seconds to account for fetching delays
        Task<List<MusicMemory>> task = TaskUtil.getTask(Queries.getAllMusicMemoriesInLastTwentyFourHours());
        List<MusicMemory> musicMemories = Tasks.await(task);
        musicMemories.forEach(musicMemory -> {
            assertTrue(musicMemory.getTimePosted().getTime() >= timestamp24HoursAgo);
        });
    }

    @Test
    public void testGetAllMusicMemoriesWithSpotifyArtistId_success() throws ExecutionException, InterruptedException {
        Task<List<MusicMemory>> task = TaskUtil.getTask(Queries.getAllMusicMemoriesWithSpotifyArtistId(
                TestDataStore.SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE));
        List<MusicMemory> musicMemories = Tasks.await(task);
        musicMemories.forEach(musicMemory -> {
            assertEquals(musicMemory.getSong().getSpotifyArtistId(),
                    TestDataStore.SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE);
        });
    }

    @Test
    public void testGetMostPopularSongsByArtist_success() throws ExecutionException, InterruptedException {
        Task<List<SongCount>> task = TaskUtil.getTask(Queries.getMostPopularSongsByArtist(
                TestDataStore.SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE, 5));
        List<SongCount> songCounts = Tasks.await(task);
        AtomicLong currentCount = new AtomicLong(Long.MAX_VALUE);
        songCounts.forEach(songCount -> {
            assertEquals(songCount.getSong().getSpotifyArtistId(),
                    TestDataStore.SPOTIFY_ARTIST_ID_THAT_EXISTS_IN_FIREBASE);
            assertTrue(songCount.getCount() < currentCount.get());
            currentCount.set(songCount.getCount());
        });
    }

}
