package com.example.musicmap.util.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Queries {

    public static Task<QuerySnapshot> getUsersWithUsername(FirebaseFirestore firestore,
                                                           String username) {
        Query query = firestore.collection("Users").whereEqualTo("username", username);
        return query.get();
    }
}
