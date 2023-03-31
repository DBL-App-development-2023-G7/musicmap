package com.example.musicmap.util.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QueriesTest {

    private FirebaseFirestore firebaseFirestore;


    @Before
    public void setup() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Test
    public void testGetUsersByUsername() {
        Task<QuerySnapshot>
    }

}
