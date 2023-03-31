package com.example.musicmap.util.firebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ExecutionException;

public class QueriesTest {

    private String userNameThatExistsInFirebase = "username";
    private String userNameThatDoesNotExistsInFirebase = "some-username";
    private String emailThatExistsInFirebase = "pradyumanreal7@gmail.com";
    private String emailThatDoesnotExistInFirebase = "wrong@wrong";

    @Test
    public void testGetUsersByUsername_success_usernameExists() throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> task = Queries.getUsersWithUsername(userNameThatExistsInFirebase);
        QuerySnapshot querySnapshot = Tasks.await(task);
        assertFalse(querySnapshot.isEmpty());
        assertEquals(querySnapshot.getDocuments().size(), 1);
        assertEquals(querySnapshot.getDocuments().get(0).get("username"), userNameThatExistsInFirebase);
    }

    @Test
    public void testGetUsersByUsername_success_usernameDoesNotExist() throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> task = Queries.getUsersWithUsername(userNameThatDoesNotExistsInFirebase);
        QuerySnapshot querySnapshot = Tasks.await(task);
        assertTrue(querySnapshot.isEmpty());
    }

    @Test
    public void testGetUsersByUsername_success_emailExists() throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> task = Queries.getUserWithEmail(emailThatExistsInFirebase);
        QuerySnapshot querySnapshot = Tasks.await(task);
        assertFalse(querySnapshot.isEmpty());
        assertEquals(querySnapshot.getDocuments().size(), 1);
        assertEquals(querySnapshot.getDocuments().get(0).get("email"), emailThatExistsInFirebase);
    }

    @Test
    public void testGetUsersByUsername_success_emailDoesNotExist() throws ExecutionException, InterruptedException {
        Task<QuerySnapshot> task = Queries.getUserWithEmail(emailThatDoesnotExistInFirebase);
        QuerySnapshot querySnapshot = Tasks.await(task);
        assertTrue(querySnapshot.isEmpty());
    }

}
