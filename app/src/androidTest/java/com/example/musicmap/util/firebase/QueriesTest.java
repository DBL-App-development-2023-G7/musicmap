package com.example.musicmap.util.firebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.musicmap.TestDataStore;
import com.example.musicmap.feed.MusicMemory;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class QueriesTest {

    @Test
    public void testGetUsersByUsername_success_exists() throws ExecutionException, InterruptedException {
        String userNameThatExistsInFirebase = "TPGamesNL";
        Task<QuerySnapshot> task = Queries.getUsersWithUsername(userNameThatExistsInFirebase);
        QuerySnapshot querySnapshot = Tasks.await(task);
        assertFalse(querySnapshot.isEmpty());
        assertEquals(querySnapshot.getDocuments().size(), 1);
        assertEquals(querySnapshot.getDocuments().get(0).get("username"), userNameThatExistsInFirebase);
    }

    @Test
    public void testGetUsersByUsername_success_doesNotExist() throws ExecutionException, InterruptedException {
        String userNameThatDoesNotExistsInFirebase = "some-username";
        Task<QuerySnapshot> task = Queries.getUsersWithUsername(userNameThatDoesNotExistsInFirebase);
        QuerySnapshot querySnapshot = Tasks.await(task);
        assertTrue(querySnapshot.isEmpty());
    }

    @Test
    public void testGetUsersByEmail_success_exists() throws ExecutionException, InterruptedException {
        String emailThatExistsInFirebase = "pradyumanreal7@gmail.com";
        Task<QuerySnapshot> task = Queries.getUserWithEmail(emailThatExistsInFirebase);
        QuerySnapshot querySnapshot = Tasks.await(task);
        assertFalse(querySnapshot.isEmpty());
        assertEquals(querySnapshot.getDocuments().size(), 1);
        assertEquals(querySnapshot.getDocuments().get(0).get("email"), emailThatExistsInFirebase);
    }

    @Test
    public void testGetUsersByEmail_success_doesNotExist() throws ExecutionException, InterruptedException {
        String emailThatDoesnotExistInFirebase = "wrong@wrong";
        Task<QuerySnapshot> task = Queries.getUserWithEmail(emailThatDoesnotExistInFirebase);
        QuerySnapshot querySnapshot = Tasks.await(task);
        assertTrue(querySnapshot.isEmpty());
    }

    @Test
    public void testGetMusicMemoryByAuthorIdAndId_success_exists() throws ExecutionException, InterruptedException {
        String musicMemoryIdThatExistInFirebase = "N4yBvYoo76MzL0WSLqhd";
        Task<MusicMemory> task = Queries.getMusicMemoryByAuthorIdAndId(TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE,
                musicMemoryIdThatExistInFirebase);
        MusicMemory musicMemory = Tasks.await(task);
        assertNotNull(musicMemory);
        assertEquals(musicMemory.getAuthorUid(), TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE);
        assertEquals(musicMemory.getUid(), musicMemoryIdThatExistInFirebase);
    }

    @Test
    public void testGetMusicMemoriesByAuthorId_success_exists() throws ExecutionException, InterruptedException {
        Task<List<MusicMemory>> task = Queries.getMusicMemoriesByAuthorId(
                TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE);
        List<MusicMemory> musicMemories = Tasks.await(task);
        assertFalse(musicMemories.isEmpty());
        assertEquals(musicMemories.get(0).getAuthorUid(), TestDataStore.AUTHOR_UID_THAT_EXISTS_IN_FIREBASE);
    }

    @Test
    public void testGetAllMusicMemories_success_exists() throws ExecutionException, InterruptedException {
        Task<List<MusicMemory>> task = Queries.getAllMusicMemories();
        List<MusicMemory> musicMemories = Tasks.await(task);
        assertFalse(musicMemories.isEmpty());
    }

}
