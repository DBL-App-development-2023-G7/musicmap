package com.example.musicmap.util.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Queries {

    public static Task<QuerySnapshot> getUsersWithUsername(String username) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("Users").whereEqualTo("username", username);
        return query.get();
    }

    public static Task<QuerySnapshot> getUserWithEmail(String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("Users").whereEqualTo("email", email);
        return query.get();
    }

}
